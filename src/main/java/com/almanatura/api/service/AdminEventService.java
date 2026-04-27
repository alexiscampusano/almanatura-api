package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateEventRequest;
import com.almanatura.api.dto.EventResponse;
import com.almanatura.api.dto.UpdateEventRequest;
import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.mapper.EventMapper;
import com.almanatura.api.repository.CulturalEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final CulturalEventRepository culturalEventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse create(CreateEventRequest request) {
        CulturalEvent entity = eventMapper.toEntity(request);
        entity.setStatus(EventStatus.DRAFT);
        CulturalEvent saved = culturalEventRepository.save(entity);
        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return culturalEventRepository.findAllByOrderByStartsAtAsc().stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getById(long id) {
        CulturalEvent entity =
                culturalEventRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("CulturalEvent", id));
        return eventMapper.toResponse(entity);
    }

    @Transactional
    public EventResponse update(long id, UpdateEventRequest request) {
        CulturalEvent entity =
                culturalEventRepository
                        .findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("CulturalEvent", id));
        eventMapper.updateEntity(request, entity);
        CulturalEvent saved = culturalEventRepository.save(entity);
        return eventMapper.toResponse(saved);
    }

    @Transactional
    public void delete(long id) {
        if (!culturalEventRepository.existsById(id)) {
            throw ResourceNotFoundException.of("CulturalEvent", id);
        }
        culturalEventRepository.deleteById(id);
    }
}
