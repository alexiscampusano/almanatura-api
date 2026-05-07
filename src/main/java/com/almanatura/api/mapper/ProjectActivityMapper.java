package com.almanatura.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.almanatura.api.dto.ActivityParticipationResponse;
import com.almanatura.api.dto.CreateProjectActivityRequest;
import com.almanatura.api.dto.OutboundNotificationResponse;
import com.almanatura.api.dto.ProjectActivityResponse;
import com.almanatura.api.dto.ProjectImpactEntryResponse;
import com.almanatura.api.dto.PublicProjectActivityResponse;
import com.almanatura.api.dto.UpdateProjectActivityRequest;
import com.almanatura.api.entity.ActivityParticipation;
import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.entity.ProjectActivity;
import com.almanatura.api.entity.ProjectImpactEntry;

@Mapper(componentModel = "spring")
public interface ProjectActivityMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(
            target = "status",
            expression =
                    "java(request.status() == null ?"
                            + " com.almanatura.api.enums.ProjectActivityStatus.SCHEDULED :"
                            + " request.status())")
    ProjectActivity toEntity(CreateProjectActivityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    void updateEntity(UpdateProjectActivityRequest request, @MappingTarget ProjectActivity entity);

    @Mapping(target = "projectId", source = "project.id")
    ProjectActivityResponse toResponse(ProjectActivity entity);

    PublicProjectActivityResponse toPublicResponse(ProjectActivity entity);

    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "actorId", source = "actor.id")
    ActivityParticipationResponse toParticipationResponse(ActivityParticipation entity);

    OutboundNotificationResponse toNotificationResponse(OutboundNotification entity);

    @Mapping(target = "projectId", source = "project.id")
    ProjectImpactEntryResponse toImpactResponse(ProjectImpactEntry entity);
}
