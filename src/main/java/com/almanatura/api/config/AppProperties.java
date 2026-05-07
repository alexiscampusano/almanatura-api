package com.almanatura.api.config;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Strongly-typed view of every {@code app.*} property. Validation here fails fast at startup if a
 * required secret is missing.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Jwt jwt, Encryption encryption, Cors cors, Admin admin, RateLimit rateLimit) {

    public record Jwt(
            @NotBlank(message = "app.jwt.secret must be set (use APP_JWT_SECRET env var)")
                    String secret,
            @Positive long expirationMs,
            @NotBlank(message = "app.jwt.issuer must be set") String issuer) {}

    public record Encryption(
            @NotBlank(
                            message =
                                    "app.encryption.dni-key must be set (use APP_ENCRYPTION_DNI_KEY"
                                            + " env var)")
                    String dniKey) {}

    public record Cors(List<String> allowedOrigins) {}

    public record Admin(String email, String password) {}

    public record RateLimit(
            /**
             * When false (default), rate limits use {@code RemoteAddr} only (safe when exposed
             * without a trusted proxy).
             */
            boolean trustForwardedHeaders, Bucket login, Bucket register) {

        public record Bucket(@Positive int requests, @Positive int windowMinutes) {}
    }
}
