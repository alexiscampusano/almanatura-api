package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;

public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    List<CulturalEvent> findAllByOrderByStartsAtAsc();

    List<CulturalEvent> findByStatusOrderByStartsAtAsc(EventStatus status);

    Optional<CulturalEvent> findByIdAndStatus(long id, EventStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM CulturalEvent e WHERE e.id = :id AND e.status = :status")
    Optional<CulturalEvent> findByIdAndStatusForUpdate(
            @Param("id") long id, @Param("status") EventStatus status);
}
