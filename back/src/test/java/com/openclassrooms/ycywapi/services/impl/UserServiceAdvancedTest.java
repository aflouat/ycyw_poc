package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.exception.BadRequestException;
import com.openclassrooms.ycywapi.mapper.UserMapper;
import com.openclassrooms.ycywapi.models.User;
import com.openclassrooms.ycywapi.models.UserPrincipal;
import com.openclassrooms.ycywapi.payload.request.LoginRequest;
import com.openclassrooms.ycywapi.payload.request.SignupRequest;
import com.openclassrooms.ycywapi.payload.request.UserCredentialUpdateRequest;
import com.openclassrooms.ycywapi.payload.response.JwtResponse;
import com.openclassrooms.ycywapi.repositories.UserRepository;
import com.openclassrooms.ycywapi.security.JwtUtils;
import com.openclassrooms.ycywapi.services.interfaces.IIntercomService;
import com.openclassrooms.ycywapi.services.interfaces.IUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceAdvancedTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtServiceImpl jwtServiceImpl;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserMapper userMapper;
    @Mock private IUserDetailsService userDetailsService;
    @Mock private JwtUtils jwtUtils; // not used directly in UserService
    @Mock private IIntercomService intercomService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .email("john.doe@test.com")
                .username("johndoe")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_success_withIntercom() {
        LoginRequest req = new LoginRequest();
        req.setIdentifier("john.doe@test.com");
        req.setPassword("pwd");

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                UserPrincipal.builder().id(1L).email(user.getEmail()).username(user.getUsername()).password(user.getPassword()).build(),
                null,
                List.of(() -> "USER")
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(jwtServiceImpl.generateToken(any(UserPrincipal.class))).thenReturn("app.jwt");
        when(userRepository.findByUsername("johndoe")).thenReturn(user);
        when(intercomService.generateIdentityVerificationJwt(any(UserPrincipal.class))).thenReturn("intercom.jwt");
        when(intercomService.generateUserHash("1")).thenReturn("abc123");

        JwtResponse response = userService.authenticate(req);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("john.doe@test.com", response.getEmail());
        assertEquals("johndoe", response.getUsername());
        assertEquals("app.jwt", response.getToken());
        assertEquals("intercom.jwt", response.getIntercomJwt());
        assertEquals("abc123", response.getIntercomUserHash());
    }

    @Test
    void authenticate_intercomFailure_isHandled() {
        LoginRequest req = new LoginRequest();
        req.setIdentifier("john.doe@test.com");
        req.setPassword("pwd");

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                UserPrincipal.builder().id(1L).email(user.getEmail()).username(user.getUsername()).password(user.getPassword()).build(),
                null,
                List.of(() -> "USER")
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(jwtServiceImpl.generateToken(any(UserPrincipal.class))).thenReturn("app.jwt");
        when(userRepository.findByUsername("johndoe")).thenReturn(user);
        when(intercomService.generateIdentityVerificationJwt(any(UserPrincipal.class))).thenThrow(new IllegalStateException("not configured"));
        // user hash will not be called in this branch but safe to let default null

        JwtResponse response = userService.authenticate(req);
        assertNotNull(response);
        assertNull(response.getIntercomJwt()); // since it failed
    }

    @Test
    void authenticate_sameIntercomAndAppToken_dropsIntercomJwt() {
        LoginRequest req = new LoginRequest();
        req.setIdentifier("john.doe@test.com");
        req.setPassword("pwd");

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                UserPrincipal.builder().id(1L).email(user.getEmail()).username(user.getUsername()).password(user.getPassword()).build(),
                null,
                List.of(() -> "USER")
        );
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(jwtServiceImpl.generateToken(any(UserPrincipal.class))).thenReturn("SAME");
        when(userRepository.findByUsername("johndoe")).thenReturn(user);
        when(intercomService.generateIdentityVerificationJwt(any(UserPrincipal.class))).thenReturn("SAME");
        when(intercomService.generateUserHash("1")).thenReturn("xyz");

        JwtResponse response = userService.authenticate(req);
        assertNotNull(response);
        assertEquals("SAME", response.getToken());
        assertNull(response.getIntercomJwt()); // dropped
        assertEquals("xyz", response.getIntercomUserHash());
    }

    @Test
    void getConnectedUser_authenticated_returnsUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("john.doe@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);

        User current = userService.getConnectedUser();
        assertNotNull(current);
        assertEquals("johndoe", current.getUsername());
    }

    @Test
    void getConnectedUser_unauthenticated_throws() {
        SecurityContextHolder.clearContext();
        assertThrows(BadRequestException.class, () -> userService.getConnectedUser());
    }

    @Test
    void updateUser_conflicts_throwIllegalArgument() {
        // prepare connected user
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("john.doe@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);

        // conflict email used by another user
        User otherByEmail = User.builder().id(2L).email("new@test.com").username("x").password("p").build();
        when(userRepository.findByEmail("new@test.com")).thenReturn(otherByEmail);
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        UserCredentialUpdateRequest req = new UserCredentialUpdateRequest();
        req.setEmail("new@test.com");
        req.setUsername("johndoe");
        req.setPassword("");

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(req));

        // conflict username used by another user
        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        User otherByUsername = User.builder().id(3L).email("x@test.com").username("newname").password("p").build();
        when(userRepository.findByUsername("newname")).thenReturn(otherByUsername);
        req.setEmail("john.doe@test.com");
        req.setUsername("newname");
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(req));
    }

    @Test
    void updateUser_success_updatesAndSaves() {
        // connected user
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("john.doe@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        when(userRepository.findByEmail("new@test.com")).thenReturn(null);
        when(userRepository.findByUsername("newname")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCredentialUpdateRequest req = new UserCredentialUpdateRequest();
        req.setEmail("new@test.com");
        req.setUsername("newname");
        req.setPassword("newpwd");

        userService.updateUser(req);

        verify(userRepository, times(1)).save(argThat(saved ->
                "new@test.com".equals(saved.getEmail()) &&
                        "newname".equals(saved.getUsername()) &&
                        !"newpwd".equals(saved.getPassword()) && // password must be encoded
                        saved.getPassword() != null && saved.getPassword().startsWith("$2")
        ));
    }

    @Test
    void getConnectedUserJwtResponse_populatesIntercomArtifacts() {
        // set connected user context
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("john.doe@test.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        when(intercomService.generateIdentityVerificationJwt(any(UserPrincipal.class))).thenReturn("ijwt");
        when(intercomService.generateUserHash("1")).thenReturn("hash");

        JwtResponse res = userService.getConnectedUserJwtResponse();
        assertNotNull(res);
        assertEquals("ijwt", res.getIntercomJwt());
        assertEquals("hash", res.getIntercomUserHash());
        assertEquals("johndoe", res.getUsername());
    }

    @Test
    void fetchUserByToken_delegatesToJwtServiceAndRepo() {
        when(jwtServiceImpl.extractIdentifier("tkn")).thenReturn("john.doe@test.com");
        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        User res = userService.fetchUserByToken("tkn");
        assertSame(user, res);
        verify(jwtServiceImpl, times(1)).extractIdentifier("tkn");
        verify(userRepository, times(1)).findByEmail("john.doe@test.com");
    }

    @Test
    void register_throwsWhenUserExists_elseSaves() {
        SignupRequest s = new SignupRequest();
        s.setEmail("john.doe@test.com");
        s.setUsername("johndoe");
        s.setPassword("pwd");

        when(userDetailsService.isUserExists("john.doe@test.com")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.register(s));

        // not exists path
        when(userDetailsService.isUserExists("john.doe@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        userService.register(s);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
