package com.openclassrooms.ycywapi.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtUtilsTest {

    @Test
    void extractTokenFromRequest_shouldReturnEmpty_whenNoHeader() {
        JwtUtils utils = new JwtUtils();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn(null);
        String token = utils.extractTokenFromRequest(req);
        assertNotNull(token);
        assertEquals("", token);
    }

    @Test
    void extractTokenFromRequest_shouldReturnEmpty_whenHeaderNotBearer() {
        JwtUtils utils = new JwtUtils();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Basic abcdef");
        String token = utils.extractTokenFromRequest(req);
        assertEquals("", token);
    }

    @Test
    void extractTokenFromRequest_shouldReturnToken_whenBearerHeader() {
        JwtUtils utils = new JwtUtils();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer my.jwt.token");
        String token = utils.extractTokenFromRequest(req);
        assertEquals("my.jwt.token", token);
    }
}
