package com.almanatura.api.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.almanatura.api.config.AppProperties;
import com.almanatura.api.exception.ApiErrorWriter;
import com.almanatura.api.exception.ErrorCode;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * In-memory rate limiting using Bucket4j.
 *
 * <p>Single-node deployments only; if the API is scaled horizontally the buckets must be moved to a
 * shared store (e.g. Redis via {@code bucket4j-redis}). Configurable via {@code app.rate-limit.*}
 * properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTER_PATH_PATTERN = "/events/*/register";
    private static final String RETRY_AFTER_SECONDS = "60";

    private final AppProperties properties;
    private final ApiErrorWriter errorWriter;

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Bucket bucket = resolveBucket(request);
        if (bucket == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn(
                    "Rate limit exceeded for {} from {}",
                    request.getRequestURI(),
                    clientIp(request));
            response.setHeader(HttpHeaders.RETRY_AFTER, RETRY_AFTER_SECONDS);
            errorWriter.write(
                    request,
                    response,
                    ErrorCode.RATE_LIMIT_EXCEEDED,
                    "Too many requests. Please try again later.");
        }
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return null;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        String ip = clientIp(request);

        if (LOGIN_PATH.equals(path)) {
            AppProperties.RateLimit.Bucket cfg = properties.rateLimit().login();
            return loginBuckets.computeIfAbsent(ip, k -> newBucket(cfg));
        }

        if (PATH_MATCHER.match(REGISTER_PATH_PATTERN, path)) {
            AppProperties.RateLimit.Bucket cfg = properties.rateLimit().register();
            return registerBuckets.computeIfAbsent(ip, k -> newBucket(cfg));
        }

        return null;
    }

    private static Bucket newBucket(AppProperties.RateLimit.Bucket cfg) {
        Bandwidth limit =
                Bandwidth.builder()
                        .capacity(cfg.requests())
                        .refillIntervally(cfg.requests(), Duration.ofMinutes(cfg.windowMinutes()))
                        .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
