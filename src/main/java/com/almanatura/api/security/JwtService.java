package com.almanatura.api.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.almanatura.api.config.AppProperties;
import com.almanatura.api.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    /** HS512 requires keys of at least 64 bytes (512 bits) per RFC 7518. */
    private static final int HS512_MIN_KEY_BYTES = 64;

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "uid";

    private final SecretKey signingKey;
    private final long expirationMs;
    private final String issuer;

    public JwtService(AppProperties properties) {
        byte[] keyBytes = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < HS512_MIN_KEY_BYTES) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least "
                            + HS512_MIN_KEY_BYTES
                            + " bytes (got "
                            + keyBytes.length
                            + "). Use a high-entropy random string for HS512.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = properties.jwt().expirationMs();
        this.issuer = properties.jwt().issuer();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .claims(
                        Map.of(
                                CLAIM_ROLE, user.getRole().name(),
                                CLAIM_USER_ID, user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey, Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        try {
            String username = extractUsername(token);
            return username.equalsIgnoreCase(expectedUsername) && !isExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims =
                Jwts.parser()
                        .verifyWith(signingKey)
                        .requireIssuer(issuer)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
        return resolver.apply(claims);
    }
}
