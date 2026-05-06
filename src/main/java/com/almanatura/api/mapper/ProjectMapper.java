package com.almanatura.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.almanatura.api.dto.CreateProjectRequest;
import com.almanatura.api.dto.ProjectResponse;
import com.almanatura.api.dto.PublicProjectResponse;
import com.almanatura.api.dto.UpdateProjectRequest;
import com.almanatura.api.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    Project toEntity(CreateProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    void updateEntity(UpdateProjectRequest request, @MappingTarget Project entity);

    ProjectResponse toResponse(Project entity);

    PublicProjectResponse toPublicResponse(Project entity);
}
