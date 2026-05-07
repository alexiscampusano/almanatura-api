package com.almanatura.api.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.ActivityParticipationResponse;
import com.almanatura.api.dto.InviteActivityParticipationRequest;
import com.almanatura.api.dto.PatchActivityParticipationRequest;
import com.almanatura.api.service.AdminActivityParticipationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/projects/{projectId}/activities/{activityId}/participations")
@RequiredArgsConstructor
@Tag(
        name = "Activity participation (admin)",
        description =
                "Invite actors to an activity and update participation status. JWT required; no"
                        + " public PII endpoints.")
public class AdminActivityParticipationController {

    private final AdminActivityParticipationService adminActivityParticipationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Invite actor to activity",
            description = "Creates participation in INVITED status.")
    public ActivityParticipationResponse invite(
            @PathVariable long projectId,
            @PathVariable long activityId,
            @Valid @RequestBody InviteActivityParticipationRequest body) {
        return adminActivityParticipationService.invite(projectId, activityId, body);
    }

    @GetMapping
    @Operation(summary = "List participations for activity")
    public List<ActivityParticipationResponse> list(
            @PathVariable long projectId, @PathVariable long activityId) {
        return adminActivityParticipationService.list(projectId, activityId);
    }

    @PatchMapping("/{participationId}")
    @Operation(summary = "Update participation status")
    public ActivityParticipationResponse patchStatus(
            @PathVariable long projectId,
            @PathVariable long activityId,
            @PathVariable long participationId,
            @Valid @RequestBody PatchActivityParticipationRequest body) {
        return adminActivityParticipationService.patchStatus(
                projectId, activityId, participationId, body);
    }
}
