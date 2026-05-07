package com.almanatura.api.controller;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Lightweight readiness probe including clock skew diagnostics. */
@RestController
@RequestMapping("/ping")
@Tag(name = "Health", description = "Public smoke-test endpoint")
public class HealthController {

    public record HealthResponse(String service, String status, Instant timestamp) {}

    @GetMapping
    @Operation(
            summary = "Liveness probe",
            description = "Returns service name and current server time.")
    public HealthResponse ping() {
        return new HealthResponse("almanatura-api", "ok", Instant.now());
    }
}
