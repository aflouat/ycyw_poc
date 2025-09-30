package com.openclassrooms.ycywapi.controllers;

import com.openclassrooms.ycywapi.payload.request.LoginRequest;
import com.openclassrooms.ycywapi.payload.request.SignupRequest;
import com.openclassrooms.ycywapi.payload.response.JwtResponse;
import com.openclassrooms.ycywapi.services.interfaces.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegisterUser_Success() {
        // given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@test.com");
        signupRequest.setPassword("password123");

        // when
        doNothing().when(userService).register(any(SignupRequest.class));
        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // then
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testAuthenticateUser_Success() {
        // given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("test@gmail.com");
        loginRequest.setPassword("pass@word");

        JwtResponse expected = JwtResponse.builder()
                .id(1L)
                .email("test@gmail.com")
                .username("test")
                .type("Bearer")
                .token("HSDFSFSF555AA:156RSA")
                .build();

        // when
        when(userService.authenticate(any(LoginRequest.class))).thenReturn(expected);
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        // then
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        JwtResponse actual = (JwtResponse) responseEntity.getBody();
        assertNotNull(actual);
        assertEquals(expected.getToken(), actual.getToken());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void testGetConnectedUserInformation_Success() {
        // given
        JwtResponse expected = JwtResponse.builder()
                .id(2L)
                .email("connected@test.com")
                .username("connected")
                .type("Bearer")
                .token("TOKEN_123")
                .build();

        // when
        when(userService.getConnectedUserJwtResponse()).thenReturn(expected);
        ResponseEntity<?> response = authController.getConnectedUserInformation();

        // then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        JwtResponse body = (JwtResponse) response.getBody();
        assertNotNull(body);
        assertEquals(expected.getToken(), body.getToken());
        assertEquals(expected.getEmail(), body.getEmail());
    }
}