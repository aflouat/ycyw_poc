package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.models.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private String base64Secret;

    @BeforeEach
    void init() {
        jwtService = new JwtServiceImpl();
        // Build a 64-byte secret and Base64-encode it (jjwt expects base64 text for our getKey implementation)
        byte[] raw = "this-is-a-long-secret-used-for-unit-tests-which-should-be-very-strong-0123456789".getBytes(StandardCharsets.UTF_8);
        base64Secret = Base64.getEncoder().encodeToString(raw);
        ReflectionTestUtils.setField(jwtService, "secretKey", base64Secret);
    }

    private UserPrincipal samplePrincipal() {
        return UserPrincipal.builder()
                .id(99L)
                .email("alice@test.com")
                .username("alice")
                .password("pwd")
                .build();
    }

    @Test
    void generateToken_shouldContainExpectedClaims_andBeVerifiable() {
        UserPrincipal user = samplePrincipal();
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());

        // Verify signature and read claims using the same key
        SecretKey key = jwtService.getKey();
        var jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        var claims = jws.getPayload();

        assertEquals("alice", claims.getSubject());
        assertEquals(99, ((Number) claims.get("user_id")).intValue());
        assertEquals("alice@test.com", claims.get("email"));
        Date exp = claims.getExpiration();
        assertNotNull(exp);
        assertTrue(exp.after(new Date()));
    }

    @Test
    void extractIdentifier_shouldReturnSubject() {
        UserPrincipal user = samplePrincipal();
        String token = jwtService.generateToken(user);
        String subject = jwtService.extractIdentifier(token);
        // Implementation uses subject = username
        assertEquals("alice", subject);
    }

    @Test
    void hasTokenNotExpired_shouldReflectExpiration() {
        // Fresh token should not be expired
        String fresh = jwtService.generateToken(samplePrincipal());
        assertTrue(jwtService.hasTokenNotExpired(fresh));

        // Build an expired token using the same secret
        SecretKey key = jwtService.getKey();
        String expired = Jwts.builder()
                .subject("alice")
                .issuedAt(new Date(System.currentTimeMillis() - 3_600_000))
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        // Implementation will throw ExpiredJwtException when parsing expired token
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.hasTokenNotExpired(expired));
    }

    @Test
    void getKey_shouldReturnKeyDerivedFromBase64Secret() {
        SecretKey key = jwtService.getKey();
        assertNotNull(key);
        // Verify we can use it to sign a token and then parse
        String t = Jwts.builder()
                .subject("test")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10_000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        var parsed = Jwts.parser().verifyWith(key).build().parseSignedClaims(t);
        assertEquals("test", parsed.getPayload().getSubject());
    }
}
