package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.PublicEventResponse;
import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.EventMapper;
import com.almanatura.api.repository.CulturalEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicEventService {

    private final CulturalEventRepository culturalEventRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<PublicEventResponse> listPublished() {
        return culturalEventRepository
                .findByStatusOrderByStartsAtAsc(EventStatus.PUBLISHED)
                .stream()
                .map(eventMapper::toPublicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicEventResponse getPublished(long id) {
        CulturalEvent entity =
                culturalEventRepository
                        .findByIdAndStatus(id, EventStatus.PUBLISHED)
                        .orElseThrow(() -> ResourceNotFoundException.of("CulturalEvent", id));
        return eventMapper.toPublicResponse(entity);
    }
}
