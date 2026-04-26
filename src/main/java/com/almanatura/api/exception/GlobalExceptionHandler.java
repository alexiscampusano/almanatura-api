package com.almanatura.api.exception;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized HTTP error mapping for the entire API.
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} so Spring's built-in handlers for the standard
 * MVC exceptions stay in place and we only override them to inject the cross-cutting properties
 * ({@code code}, {@code traceId}, {@code timestamp}) via {@link ApiProblems#decorate}.
 *
 * <p>Custom application exceptions get their own {@link ExceptionHandler}. The catch-all for {@link
 * Exception} is the only place that logs the stack trace; everything else is either an expected
 * client error or already mapped by Spring.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ApiProblems apiProblems;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return entity(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUse(
            EmailAlreadyInUseException ex, HttpServletRequest request) {
        return entity(ErrorCode.EMAIL_ALREADY_IN_USE, ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        log.debug("Authentication failure: {}", ex.getMessage());
        return entity(
                ErrorCode.INVALID_CREDENTIALS, "The provided credentials are invalid", request);
    }

    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ProblemDetail> handleAccountDisabled(
            AccountStatusException ex, HttpServletRequest request) {
        log.info("Login attempt against disabled or locked account");
        return entity(
                ErrorCode.ACCOUNT_DISABLED,
                "This account is disabled. Contact your administrator.",
                request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        log.debug("Authentication exception: {}", ex.getMessage());
        return entity(
                ErrorCode.AUTHENTICATION_REQUIRED,
                "Authentication is required to access this resource",
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        log.debug("Access denied: {}", ex.getMessage());
        return entity(
                ErrorCode.ACCESS_DENIED,
                "You do not have permission to access this resource",
                request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldViolation> violations =
                ex.getConstraintViolations().stream()
                        .map(GlobalExceptionHandler::toViolation)
                        .toList();
        ProblemDetail body =
                apiProblems.of(
                        ErrorCode.VALIDATION_FAILED,
                        "One or more fields are invalid",
                        request,
                        violations);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {} {}", request.getMethod(), request.getRequestURI(), ex);
        return entity(
                ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred. Please contact support if the problem persists.",
                request);
    }

    // ---------------------------------------------------------------------
    // Overrides of Spring's built-in handlers (ResponseEntityExceptionHandler)
    // ---------------------------------------------------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<FieldViolation> violations =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(GlobalExceptionHandler::toViolation)
                        .toList();
        ProblemDetail body =
                apiProblems.of(
                        ErrorCode.VALIDATION_FAILED,
                        "One or more fields are invalid",
                        servletRequest(request),
                        violations);
        return new ResponseEntity<>(body, headers, body.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.VALIDATION_FAILED, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.MALFORMED_REQUEST, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.MISSING_PARAMETER, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.TYPE_MISMATCH, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.METHOD_NOT_ALLOWED, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.MEDIA_TYPE_NOT_SUPPORTED, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.RESOURCE_NOT_FOUND, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return decorated(ex, ErrorCode.RESOURCE_NOT_FOUND, headers, request);
    }

    private ResponseEntity<Object> decorated(
            Exception ex, ErrorCode code, HttpHeaders headers, WebRequest request) {
        ProblemDetail base = ProblemDetail.forStatusAndDetail(code.status(), code.title());
        ProblemDetail body = apiProblems.decorate(base, code, servletRequest(request));
        return new ResponseEntity<>(body, headers, body.getStatus());
    }

    private ResponseEntity<ProblemDetail> entity(
            ErrorCode code, String detail, HttpServletRequest request) {
        ProblemDetail body = apiProblems.of(code, detail, request);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    private static FieldViolation toViolation(FieldError fe) {
        return new FieldViolation(
                fe.getField(), fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage());
    }

    private static FieldViolation toViolation(ConstraintViolation<?> cv) {
        String field = cv.getPropertyPath() == null ? "" : cv.getPropertyPath().toString();
        return new FieldViolation(field, cv.getMessage());
    }

    private static HttpServletRequest servletRequest(WebRequest request) {
        if (request instanceof org.springframework.web.context.request.ServletWebRequest swr) {
            return swr.getRequest();
        }
        return null;
    }
}
