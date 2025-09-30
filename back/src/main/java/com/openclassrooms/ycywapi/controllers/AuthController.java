package com.openclassrooms.ycywapi.controllers;

import com.openclassrooms.ycywapi.payload.request.LoginRequest;
import com.openclassrooms.ycywapi.payload.request.SignupRequest;
import com.openclassrooms.ycywapi.payload.request.UserCredentialUpdateRequest;
import com.openclassrooms.ycywapi.payload.response.JwtResponse;
import com.openclassrooms.ycywapi.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse;
        jwtResponse = userService.authenticate(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
            userService.register(signUpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("me")
    public ResponseEntity<?> getConnectedUserInformation() {
        JwtResponse jwtResponse = userService.getConnectedUserJwtResponse();
        logger.debug("jwtResponse:"+jwtResponse);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserCredentialUpdateRequest userCredentialUpdateRequest) {
        userService.updateUser(userCredentialUpdateRequest);
        return ResponseEntity.ok().build();
    }
}