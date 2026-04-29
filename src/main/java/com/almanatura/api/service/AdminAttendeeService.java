package com.almanatura.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.AdminAttendeeResponse;
import com.almanatura.api.entity.EventAttendee;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.EventAttendeeRepository;
import com.almanatura.api.util.DniCipherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAttendeeService {

    private final CulturalEventRepository culturalEventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;
    private final DniCipherService dniCipherService;

    @Transactional(readOnly = true)
    public List<AdminAttendeeResponse> listForEvent(long eventId) {
        if (!culturalEventRepository.existsById(eventId)) {
            throw ResourceNotFoundException.of("CulturalEvent", eventId);
        }
        return eventAttendeeRepository.findByCulturalEvent_IdOrderByCreatedAtAsc(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AdminAttendeeResponse toResponse(EventAttendee entity) {
        return new AdminAttendeeResponse(
                entity.getId(),
                entity.getCulturalEvent().getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                dniCipherService.decrypt(entity.getDniEncrypted()),
                entity.getCreatedAt());
    }
}
