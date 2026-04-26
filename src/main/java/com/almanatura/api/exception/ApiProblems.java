package com.almanatura.api.exception;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

/**
 * Single point that builds {@link ProblemDetail} instances for the API.
 *
 * <p>Every error response goes through here, so cross-cutting properties (machine-readable {@code
 * code}, {@code traceId} from the MDC, {@code timestamp}, {@code instance} URI and optional {@code
 * violations}) are guaranteed to be set consistently.
 */
@Component
public class ApiProblems {

    private static final String PROP_CODE = "code";
    private static final String PROP_TRACE_ID = "traceId";
    private static final String PROP_TIMESTAMP = "timestamp";
    private static final String PROP_VIOLATIONS = "violations";
    private static final String MDC_TRACE_ID = "traceId";

    public ProblemDetail of(ErrorCode code, String detail, HttpServletRequest request) {
        return of(code, detail, request, List.of());
    }

    public ProblemDetail of(
            ErrorCode code,
            String detail,
            HttpServletRequest request,
            List<FieldViolation> violations) {
        ProblemDetail problem = ProblemDetail.forStatus(code.status());
        problem.setType(code.type());
        problem.setTitle(code.title());
        problem.setDetail(detail);
        problem.setInstance(instance(request));
        problem.setProperty(PROP_CODE, code.code());
        problem.setProperty(PROP_TIMESTAMP, Instant.now().toString());

        String traceId = MDC.get(MDC_TRACE_ID);
        if (traceId != null && !traceId.isBlank()) {
            problem.setProperty(PROP_TRACE_ID, traceId);
        }
        if (violations != null && !violations.isEmpty()) {
            problem.setProperty(PROP_VIOLATIONS, violations);
        }
        return problem;
    }

    /**
     * Re-decorates a {@link ProblemDetail} produced by Spring's {@code
     * ResponseEntityExceptionHandler} with the cross-cutting properties (code, traceId, timestamp).
     * Used by the global handler when overriding Spring's default behavior to keep the request
     * context already resolved by the framework.
     */
    public ProblemDetail decorate(
            ProblemDetail problem, ErrorCode code, HttpServletRequest request) {
        problem.setType(code.type());
        problem.setTitle(code.title());
        if (problem.getInstance() == null) {
            problem.setInstance(instance(request));
        }
        problem.setProperty(PROP_CODE, code.code());
        problem.setProperty(PROP_TIMESTAMP, Instant.now().toString());
        String traceId = MDC.get(MDC_TRACE_ID);
        if (traceId != null && !traceId.isBlank()) {
            problem.setProperty(PROP_TRACE_ID, traceId);
        }
        return problem;
    }

    private static URI instance(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return URI.create(request.getRequestURI());
    }
}
