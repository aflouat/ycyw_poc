package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.models.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IntercomServiceTest {

    private IntercomService intercomService;
    private static final String SECRET = "this-is-a-very-strong-intercom-secret-key-32b-min"; // >= 32 bytes

    @BeforeEach
    void init() {
        intercomService = new IntercomService();
        // set defaults suitable for tests
        ReflectionTestUtils.setField(intercomService, "intercomIdentitySecret", SECRET);
        ReflectionTestUtils.setField(intercomService, "jwtTtlSeconds", 60L);
    }

    @Test
    void generateIdentityVerificationJwt_shouldThrow_whenSecretMissing() {
        ReflectionTestUtils.setField(intercomService, "intercomIdentitySecret", "");
        UserPrincipal user = UserPrincipal.builder()
                .id(10L)
                .email("user@test.com")
                .username("user")
                .password("pwd")
                .build();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> intercomService.generateIdentityVerificationJwt(user));
        assertTrue(ex.getMessage().toLowerCase().contains("secret"));
    }

    @Test
    void generateIdentityVerificationJwt_shouldContainExpectedClaims_andBeVerifiable() {
        UserPrincipal user = UserPrincipal.builder()
                .id(42L)
                .email("john.doe@test.com")
                .username("johndoe")
                .password("pwd")
                .build();

        String token = intercomService.generateIdentityVerificationJwt(user);
        assertNotNull(token);
        assertFalse(token.isBlank());

        // verify signature and read claims
        SecretKey verifyKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        var jws = Jwts.parser().verifyWith(verifyKey).build().parseSignedClaims(token);
        var claims = jws.getPayload();

        assertEquals(42, ((Number) claims.get("user_id")).intValue());
        assertEquals("john.doe@test.com", claims.get("email"));
        Date exp = claims.getExpiration();
        assertNotNull(exp);
        assertTrue(exp.after(new Date(System.currentTimeMillis() + 30_000 - 60_000))); // exp should be in future
    }

    @Test
    void generateUserHash_shouldThrow_whenSecretMissing() {
        ReflectionTestUtils.setField(intercomService, "intercomIdentitySecret", null);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> intercomService.generateUserHash("123"));
        assertTrue(ex.getMessage().toLowerCase().contains("secret"));
    }

    @Test
    void generateUserHash_shouldMatchExpectedHex() throws Exception {
        String identifier = "12345";
        // compute expected HMAC-SHA256 using same algorithm
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(identifier.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) sb.append(String.format("%02x", b));
        String expectedHex = sb.toString();

        String actual = intercomService.generateUserHash(identifier);
        assertEquals(expectedHex, actual);
    }
}
