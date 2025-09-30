package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.models.User;
import com.openclassrooms.ycywapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    // UserService has many constructor dependencies; only UserRepository methods are used
    // in the methods we test (getUserById, delete). Mockito will inject nulls for others.
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@user.com")
                .username("testuser")
                .password("password")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        // when
        userService.delete(1);
        // then
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        // when
        User found = userService.getUserById(1);
        // then
        assertNotNull(found);
        assertSame(user, found);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getUserById_shouldReturnNull_whenNotFound() {
        // given
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        // when
        User found = userService.getUserById(2);
        // then
        assertNull(found);
        verify(userRepository, times(1)).findById(2);
    }
}