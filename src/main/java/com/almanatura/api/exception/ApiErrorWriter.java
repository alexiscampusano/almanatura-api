package com.almanatura.api.exception;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Serializes a {@link ProblemDetail} straight to the {@link HttpServletResponse}.
 *
 * <p>Required for components that run outside the {@code @RestControllerAdvice} pipeline -- the
 * Spring Security entry point/access denied handlers and the {@code RateLimitFilter}. Centralizing
 * the response envelope here keeps {@code application/problem+json} as the single source of truth
 * for the error wire format.
 */
@Component
@RequiredArgsConstructor
public class ApiErrorWriter {

    private final ApiProblems apiProblems;
    private final ObjectMapper objectMapper;

    public void write(
            HttpServletRequest request, HttpServletResponse response, ErrorCode code, String detail)
            throws IOException {
        ProblemDetail problem = apiProblems.of(code, detail, request);
        write(response, problem);
    }

    public void write(HttpServletResponse response, ProblemDetail problem) throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
