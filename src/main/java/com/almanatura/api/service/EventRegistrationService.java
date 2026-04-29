package com.almanatura.api.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.RegisterAttendeeRequest;
import com.almanatura.api.dto.RegistrationResponse;
import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.entity.EventAttendee;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.exception.AttendeeAlreadyRegisteredException;
import com.almanatura.api.exception.EventAtCapacityException;
import com.almanatura.api.exception.ResourceNotFoundException;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.EventAttendeeRepository;
import com.almanatura.api.util.DniCipherService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final CulturalEventRepository culturalEventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;
    private final DniCipherService dniCipherService;

    @Transactional
    public RegistrationResponse register(long eventId, RegisterAttendeeRequest request) {
        CulturalEvent event =
                culturalEventRepository
                        .findByIdAndStatusForUpdate(eventId, EventStatus.PUBLISHED)
                        .orElseThrow(() -> ResourceNotFoundException.of("CulturalEvent", eventId));

        Integer maxAttendees = event.getMaxAttendees();
        if (maxAttendees != null) {
            long registered = eventAttendeeRepository.countByCulturalEvent_Id(eventId);
            if (registered >= maxAttendees) {
                throw new EventAtCapacityException();
            }
        }

        String emailNormalized = request.email().trim().toLowerCase(Locale.ROOT);
        if (eventAttendeeRepository.existsByCulturalEvent_IdAndEmail(eventId, emailNormalized)) {
            throw new AttendeeAlreadyRegisteredException();
        }

        EventAttendee attendee =
                EventAttendee.builder()
                        .culturalEvent(event)
                        .fullName(request.fullName().trim())
                        .email(emailNormalized)
                        .phone(
                                request.phone() == null || request.phone().isBlank()
                                        ? null
                                        : request.phone().trim())
                        .dniEncrypted(dniCipherService.encrypt(request.dni().trim()))
                        .build();

        EventAttendee saved = eventAttendeeRepository.save(attendee);
        return new RegistrationResponse(saved.getId(), eventId, saved.getCreatedAt());
    }
}
