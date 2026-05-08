package com.almanatura.api.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

/**
 * Stable, machine-readable identifiers for every error the API can return.
 *
 * <p>The string {@code code} is the contract with the frontend: it is safe to switch on, map to
 * localized messages and use for analytics. The HTTP {@code status} and {@code title} are defaults
 * used by {@code ApiProblems} when building a {@link org.springframework.http.ProblemDetail}.
 *
 * <p>Add new values here (never reuse codes) and document them in the README.
 */
public enum ErrorCode {
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "Email already in use"),
    APPLICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "Application already exists for this project"),
    INVALID_APPLICATION_TRANSITION(HttpStatus.BAD_REQUEST, "Invalid application status transition"),
    PROJECT_HAS_APPLICATIONS(HttpStatus.CONFLICT, "Project has applications and cannot be deleted"),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST, "Malformed request body"),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "Missing request parameter"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "Invalid parameter type"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "Account disabled"),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported"),
    MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Media type not supported"),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Too many requests"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private static final String TYPE_BASE_URI = "https://almanatura.org/errors/";

    private final HttpStatus status;
    private final String title;
    private final URI type;

    ErrorCode(HttpStatus status, String title) {
        this.status = status;
        this.title = title;
        this.type = URI.create(TYPE_BASE_URI + name().toLowerCase().replace('_', '-'));
    }

    public String code() {
        return name();
    }

    public HttpStatus status() {
        return status;
    }

    public String title() {
        return title;
    }

    public URI type() {
        return type;
    }
}
