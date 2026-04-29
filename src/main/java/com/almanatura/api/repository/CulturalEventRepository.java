package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;

public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    List<CulturalEvent> findAllByOrderByStartsAtAsc();

    List<CulturalEvent> findByStatusOrderByStartsAtAsc(EventStatus status);

    Optional<CulturalEvent> findByIdAndStatus(long id, EventStatus status);
}
