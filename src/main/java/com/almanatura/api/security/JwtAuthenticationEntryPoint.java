package com.almanatura.api.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.almanatura.api.exception.ApiErrorWriter;
import com.almanatura.api.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * Returns an RFC 7807 {@code application/problem+json} body whenever an unauthenticated request
 * hits a protected endpoint, instead of Spring Security's default empty 401.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiErrorWriter errorWriter;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        errorWriter.write(
                request,
                response,
                ErrorCode.AUTHENTICATION_REQUIRED,
                "Authentication is required to access this resource");
    }
}
