package com.almanatura.api.dto;

/**
 * Successful authentication payload. Shape follows the OAuth2 Bearer Token Usage convention so
 * frontend clients can store and send the token with minimal mapping.
 *
 * @param accessToken signed JWT (HS512)
 * @param tokenType always {@code "Bearer"}
 * @param expiresIn lifetime of the token in seconds, computed from {@code app.jwt.expiration-ms}
 * @param user safe projection of the authenticated user, sparing the frontend a follow-up request
 */
public record LoginResponse(
        String accessToken, String tokenType, long expiresIn, UserSummary user) {}
