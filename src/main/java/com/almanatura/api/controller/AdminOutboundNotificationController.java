package com.almanatura.api.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.CreateOutboundNotificationRequest;
import com.almanatura.api.dto.OutboundNotificationResponse;
import com.almanatura.api.service.AdminOutboundNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notifications (admin stub)",
        description =
                "Records outbound notification intent (MVP: no delivery provider). JWT required.")
public class AdminOutboundNotificationController {

    private final AdminOutboundNotificationService adminOutboundNotificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create notification record",
            description =
                    "Persists a PENDING outbound notification row for auditing / future dispatch.")
    public OutboundNotificationResponse create(
            @Valid @RequestBody CreateOutboundNotificationRequest body) {
        return adminOutboundNotificationService.create(body);
    }
}
