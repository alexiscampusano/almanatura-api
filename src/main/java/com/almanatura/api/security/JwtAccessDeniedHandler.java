package com.almanatura.api.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.almanatura.api.exception.ApiErrorWriter;
import com.almanatura.api.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * Returns an RFC 7807 {@code application/problem+json} body whenever an authenticated user lacks
 * the required role/authority for an endpoint.
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiErrorWriter errorWriter;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {
        errorWriter.write(
                request,
                response,
                ErrorCode.ACCESS_DENIED,
                "You do not have permission to access this resource");
    }
}
