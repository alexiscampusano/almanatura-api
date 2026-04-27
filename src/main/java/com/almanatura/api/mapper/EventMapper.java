package com.almanatura.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.almanatura.api.dto.CreateEventRequest;
import com.almanatura.api.dto.EventResponse;
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
    CulturalEvent toEntity(CreateEventRequest request);

    EventResponse toResponse(CulturalEvent entity);
}
