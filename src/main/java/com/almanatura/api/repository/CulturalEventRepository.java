package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.almanatura.api.dto.EventAttendanceReportRow;
import com.almanatura.api.entity.CulturalEvent;
import com.almanatura.api.enums.EventStatus;

public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long> {

    long countByStatus(EventStatus status);

    @Query(
            """
            SELECT new com.almanatura.api.dto.EventAttendanceReportRow(
                e.id, e.title, e.startsAt, e.status, COUNT(a.id))
            FROM CulturalEvent e LEFT JOIN e.attendees a
            GROUP BY e.id, e.title, e.startsAt, e.status
            ORDER BY COUNT(a.id) DESC, e.startsAt ASC
            """)
    List<EventAttendanceReportRow> findAllOrderByAttendeeCountDescStartsAtAsc();

    List<CulturalEvent> findAllByOrderByStartsAtAsc();

    List<CulturalEvent> findByStatusOrderByStartsAtAsc(EventStatus status);

    Optional<CulturalEvent> findByIdAndStatus(long id, EventStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM CulturalEvent e WHERE e.id = :id AND e.status = :status")
    Optional<CulturalEvent> findByIdAndStatusForUpdate(
            @Param("id") long id, @Param("status") EventStatus status);
}
