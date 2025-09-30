package com.openclassrooms.ycywapi.mapper;

import com.openclassrooms.ycywapi.dto.UserDto;
import com.openclassrooms.ycywapi.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    private User user;
    private UserDto dto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        user = User.builder()
                .id(1L)
                .email("john.doe@test.com")
                .username("johndoe")
                .password("pwd")
                .createdAt(now)
                .updatedAt(now)
                .build();

        dto = UserDto.builder()
                .email("john.doe@test.com")
                .username("johndoe")
                .password("pwd")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void toDto_shouldMapBasicFields() {
        UserDto mapped = mapper.toDto(user);
        assertNotNull(mapped);
        assertEquals(user.getEmail(), mapped.getEmail());
        assertEquals(user.getUsername(), mapped.getUsername());
        // password has @JsonIgnore in DTO but mapper still copies the field
        assertEquals(user.getPassword(), mapped.getPassword());
        assertEquals(user.getCreatedAt(), mapped.getCreatedAt());
        assertEquals(user.getUpdatedAt(), mapped.getUpdatedAt());
    }

    @Test
    void toEntity_shouldMapBasicFields() {
        User mapped = mapper.toEntity(dto);
        assertNotNull(mapped);
        assertEquals(dto.getEmail(), mapped.getEmail());
        assertEquals(dto.getUsername(), mapped.getUsername());
        assertEquals(dto.getPassword(), mapped.getPassword());
        assertEquals(dto.getCreatedAt(), mapped.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), mapped.getUpdatedAt());
    }
}
