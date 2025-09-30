package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.models.User;
import com.openclassrooms.ycywapi.models.UserPrincipal;
import com.openclassrooms.ycywapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {
        // Note: the service has two UserRepository fields; pass the same mock twice
        service = new UserDetailsServiceImpl(userRepository, userRepository);
        user = User.builder()
                .id(10L)
                .email("john.doe@test.com")
                .username("johndoe")
                .password("pwd")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void loadUserByUsername_shouldReturnUserPrincipal_whenUserFound() {
        when(userRepository.findByEmail("johndoe")).thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(user);

        UserDetails details = service.loadUserByUsername("johndoe");

        assertNotNull(details);
        assertTrue(details instanceof UserPrincipal);
        UserPrincipal principal = (UserPrincipal) details;
        assertEquals(user.getId(), principal.getId());
        assertEquals(user.getUsername(), principal.getUsername());
        assertEquals(user.getEmail(), principal.getEmail());
        assertEquals(user.getPassword(), principal.getPassword());

        verify(userRepository, times(1)).findByEmail("johndoe");
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("unknown")).thenReturn(null);
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));

        verify(userRepository, times(1)).findByEmail("unknown");
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void isUserExists_shouldReturnTrue_whenEmailFound() {
        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        assertTrue(service.isUserExists("john.doe@test.com"));
        verify(userRepository, times(1)).findByEmail("john.doe@test.com");
    }

    @Test
    void isUserExists_shouldReturnFalse_whenEmailNotFound() {
        when(userRepository.findByEmail("nope@test.com")).thenReturn(null);
        assertFalse(service.isUserExists("nope@test.com"));
        verify(userRepository, times(1)).findByEmail("nope@test.com");
    }

    @Test
    void loadUserByEmail_shouldDelegateToRepository() {
        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(user);
        User found = service.loadUserByEmail("john.doe@test.com");
        assertSame(user, found);
        verify(userRepository, times(1)).findByEmail("john.doe@test.com");
    }

    @Test
    void loadUserByIdentifier_shouldTryEmailThenUsername() {
        // first, not found by email, found by username
        when(userRepository.findByEmail("johndoe")).thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(user);
        User byIdentifier = service.loadUserByIdentifier("johndoe");
        assertSame(user, byIdentifier);
        verify(userRepository, times(1)).findByEmail("johndoe");
        verify(userRepository, times(1)).findByUsername("johndoe");
    }
}
