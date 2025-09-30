package com.openclassrooms.ycywapi.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SwaggerConfigTest {

    @Test
    void customOpenAPI_shouldConfigureInfoAndSecurity() {
        SwaggerConfig cfg = new SwaggerConfig();
        OpenAPI api = cfg.customOpenAPI();
        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Your Car Your Way API", api.getInfo().getTitle());
        assertEquals("0.0.2", api.getInfo().getVersion());
        assertNotNull(api.getComponents());
        assertNotNull(api.getComponents().getSecuritySchemes());
        SecurityScheme scheme = api.getComponents().getSecuritySchemes().get("bearerAuth");
        assertNotNull(scheme);
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
        assertNotNull(api.getSecurity());
        assertTrue(api.getSecurity().stream().anyMatch(sr -> sr.containsKey("bearerAuth")));
    }
}
