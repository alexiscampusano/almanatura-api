package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;

import com.almanatura.api.dto.AdminApplicationResponse;
import com.almanatura.api.dto.PatchApplicationStatusRequest;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.service.AdminApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/** Admin REST API for reviewing and transitioning project applications. */
@RestController
@Validated
@RequestMapping("/admin/applications")
@RequiredArgsConstructor
@Tag(
        name = "Applications (admin)",
        description =
                "Review pipeline. PATCH transitions status; REGISTERED_AS_ACTOR creates the actor"
                        + " row. Includes decrypted national ID in responses — staff only.")
public class AdminApplicationController {

    private final AdminApplicationService adminApplicationService;

    @GetMapping
    @Operation(summary = "Search applications", description = "Optional projectId and status.")
    public List<AdminApplicationResponse> search(
            @RequestParam(name = "projectId", required = false) @Positive Long projectId,
            @RequestParam(name = "status", required = false) ApplicationStatus status) {
        return adminApplicationService.search(projectId, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by id")
    public AdminApplicationResponse getById(@PathVariable @Positive long id) {
        return adminApplicationService.getById(id);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Transition application status",
            description = "Invalid transitions return 400 INVALID_APPLICATION_TRANSITION.")
    public AdminApplicationResponse patchStatus(
            @PathVariable @Positive long id, @Valid @RequestBody PatchApplicationStatusRequest body) {
        return adminApplicationService.patchStatus(id, body);
    }
}
