package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.ActivityParticipationResponse;
import com.almanatura.api.dto.InviteActivityParticipationRequest;
import com.almanatura.api.dto.PatchActivityParticipationRequest;
import com.almanatura.api.entity.ActivityParticipation;
import com.almanatura.api.entity.Actor;
import com.almanatura.api.entity.ProjectActivity;
import com.almanatura.api.enums.ActivityParticipationStatus;
import com.almanatura.api.exception.ParticipationAlreadyExistsException;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.ProjectActivityMapper;
import com.almanatura.api.repository.ActivityParticipationRepository;
import com.almanatura.api.repository.ActorRepository;
import com.almanatura.api.repository.ProjectActivityRepository;

import lombok.RequiredArgsConstructor;

/** Invites actors onto activities and coordinates participation status updates. */
@Service
@RequiredArgsConstructor
public class AdminActivityParticipationService {

    private final ProjectActivityRepository projectActivityRepository;
    private final ActorRepository actorRepository;
    private final ActivityParticipationRepository activityParticipationRepository;
    private final ProjectActivityMapper projectActivityMapper;

    @Transactional
    public ActivityParticipationResponse invite(
            long projectId, long activityId, InviteActivityParticipationRequest request) {
        ProjectActivity activity = loadActivity(projectId, activityId);
        Actor actor =
                actorRepository
                        .findById(request.actorId())
                        .orElseThrow(
                                () -> ResourceNotFoundException.of("Actor", request.actorId()));
        if (activityParticipationRepository.existsByActivity_IdAndActor_Id(
                activityId, actor.getId())) {
            throw new ParticipationAlreadyExistsException();
        }
        ActivityParticipation row =
                ActivityParticipation.builder()
                        .activity(activity)
                        .actor(actor)
                        .status(ActivityParticipationStatus.INVITED)
                        .build();
        return projectActivityMapper.toParticipationResponse(
                activityParticipationRepository.save(row));
    }

    @Transactional
    public ActivityParticipationResponse patchStatus(
            long projectId,
            long activityId,
            long participationId,
            PatchActivityParticipationRequest request) {
        loadActivity(projectId, activityId);
        ActivityParticipation row =
                activityParticipationRepository
                        .findByIdAndActivity_Id(participationId, activityId)
                        .orElseThrow(
                                () ->
                                        ResourceNotFoundException.of(
                                                "ActivityParticipation", participationId));
        row.setStatus(request.status());
        return projectActivityMapper.toParticipationResponse(
                activityParticipationRepository.save(row));
    }

    @Transactional(readOnly = true)
    public List<ActivityParticipationResponse> list(long projectId, long activityId) {
        loadActivity(projectId, activityId);
        return activityParticipationRepository.findByActivity_IdOrderByIdAsc(activityId).stream()
                .map(projectActivityMapper::toParticipationResponse)
                .toList();
    }

    private ProjectActivity loadActivity(long projectId, long activityId) {
        return projectActivityRepository
                .findByIdAndProject_Id(activityId, projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("ProjectActivity", activityId));
    }
}
