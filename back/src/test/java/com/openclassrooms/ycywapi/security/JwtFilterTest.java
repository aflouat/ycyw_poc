package com.openclassrooms.ycywapi.security;

import com.openclassrooms.ycywapi.models.UserPrincipal;
import com.openclassrooms.ycywapi.services.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private JwtServiceImpl jwtServiceImpl;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldBypass_forAuthLogin() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/auth/login");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        // No authentication set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldPassThrough_whenTokenMissing() throws Exception {
        when(request.getServletPath()).thenReturn("/api/anything");
        when(jwtUtils.extractTokenFromRequest(request)).thenReturn("");

        jwtFilter.doFilterInternal(request, response, filterChain);

        // Should pass through without setting authentication
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenValidToken() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/secured");
        when(jwtUtils.extractTokenFromRequest(request)).thenReturn("token-123");
        when(jwtServiceImpl.extractIdentifier("token-123")).thenReturn("alice");
        when(jwtServiceImpl.hasTokenNotExpired("token-123")).thenReturn(true);

        UserPrincipal principal = UserPrincipal.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .password("pwd")
                .build();

        when(applicationContext.getBean(UserDetailsService.class)).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(principal);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(principal, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void validateToken_shouldThrow_whenExpired() {
        when(jwtServiceImpl.hasTokenNotExpired("expired")).thenReturn(false);
        UserPrincipal principal = UserPrincipal.builder()
                .id(2L)
                .username("bob")
                .email("bob@test.com")
                .password("pwd")
                .build();
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () ->
                jwtFilter.validateToken(request, "expired", principal));
    }
}
