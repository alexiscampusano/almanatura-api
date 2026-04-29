package com.almanatura.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.almanatura.api.dto.CreateEventRequest;
import com.almanatura.api.dto.EventResponse;
import com.almanatura.api.dto.PublicEventResponse;
import com.almanatura.api.dto.UpdateEventRequest;
import com.almanatura.api.entity.CulturalEvent;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    CulturalEvent toEntity(CreateEventRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    void updateEntity(UpdateEventRequest request, @MappingTarget CulturalEvent entity);

    EventResponse toResponse(CulturalEvent entity);

    PublicEventResponse toPublicResponse(CulturalEvent entity);
}
