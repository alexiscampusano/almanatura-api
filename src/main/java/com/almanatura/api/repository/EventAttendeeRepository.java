package com.almanatura.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.EventAttendee;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {

    long countByCulturalEvent_Id(Long culturalEventId);

    boolean existsByCulturalEvent_IdAndEmail(long culturalEventId, String email);
}
