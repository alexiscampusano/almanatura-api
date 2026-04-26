package com.almanatura.api.exception;

/**
 * One failed bean-validation constraint, surfaced inside the {@code violations} property of a
 * {@link org.springframework.http.ProblemDetail} response.
 */
public record FieldViolation(String field, String message) {}
