package com.openclassrooms.mddapi.services.impl;

import com.openclassrooms.mddapi.models.UserPrincipal;
import com.openclassrooms.mddapi.services.interfaces.IIntercomService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class IntercomService implements IIntercomService {

    private static final Logger logger = LoggerFactory.getLogger(IntercomService.class);

    // Use empty default so the bean can be created even if not configured; we'll validate on use.
    @Value("${intercom.identity.secret:}")
    private String intercomIdentitySecret;

    // Default TTL 300s (5 minutes) as recommended for identity verification tokens
    @Value("${intercom.identity.jwtTtlSeconds:300}")
    private long jwtTtlSeconds;

    @Override
    public String generateIdentityVerificationJwt(UserPrincipal user) {
        ensureSecretConfigured();
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtTtlSeconds * 1000);

        SecretKey key = Keys.hmacShaKeyFor(intercomIdentitySecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());              // Required
        claims.put("email", user.getEmail());
        String token = Jwts.builder().claims(claims)

                .issuedAt(now)
                .expiration(exp)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        logger.debug("Generated Intercom identity JWT for user {} exp={}s", user.getUsername(), jwtTtlSeconds);
        return token;
    }

    @Override
    public String generateUserHash(String identifier) {
        ensureSecretConfigured();
        if (identifier == null) {
            throw new IllegalArgumentException("identifier must not be null");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(intercomIdentitySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(identifier.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute Intercom user_hash", e);
        }
    }

    private void ensureSecretConfigured() {
        if (intercomIdentitySecret == null || intercomIdentitySecret.isEmpty()) {
            throw new IllegalStateException("Intercom identity secret is not configured. Set 'intercom.identity.secret' in application properties.");
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
