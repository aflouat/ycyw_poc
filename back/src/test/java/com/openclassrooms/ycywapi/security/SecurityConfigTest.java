package com.openclassrooms.ycywapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = SecurityConfigTest.DummyController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class SecurityConfigTest {

    @RestController
    static class DummyController {
        @GetMapping("/test/ping")
        public String ping() { return "pong"; }
    }

    @Autowired(required = false)
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @MockBean
    private JwtFilter jwtFilter; // ensure bean exists and is added to chain

    @MockBean
    private UserDetailsService userDetailsService; // required by AuthenticationProvider bean

    @Test
    void context_shouldProvideSecurityBeans() {
        assertNotNull(securityFilterChain);
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void authenticationProvider_shouldBeDaoProvider() {
        SecurityConfig config = new SecurityConfig(jwtFilter, userDetailsService);
        AuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void securityChain_shouldContainJwtFilter() {
        assertNotNull(springSecurityFilterChain);
        boolean hasJwtFilter = springSecurityFilterChain.getFilters("/any").stream()
                .anyMatch(f -> f.getClass().getName().contains("JwtFilter"));
        assertTrue(hasJwtFilter, "JwtFilter should be registered in the security filter chain");
    }
}
