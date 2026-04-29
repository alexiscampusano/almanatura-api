package com.almanatura.api.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.EventAttendanceReportRow;
import com.almanatura.api.dto.EventStatusCount;
import com.almanatura.api.dto.ReportsSummaryResponse;
import com.almanatura.api.enums.EventStatus;
import com.almanatura.api.repository.CulturalEventRepository;
import com.almanatura.api.repository.EventAttendeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final CulturalEventRepository culturalEventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;

    @Transactional(readOnly = true)
    public ReportsSummaryResponse summary() {
        List<EventStatusCount> byStatus =
                Arrays.stream(EventStatus.values())
                        .map(
                                status ->
                                        new EventStatusCount(
                                                status,
                                                culturalEventRepository.countByStatus(status)))
                        .sorted(Comparator.comparing(e -> e.status().name()))
                        .toList();
        long totalEvents = culturalEventRepository.count();
        long totalRegistrations = eventAttendeeRepository.count();
        return new ReportsSummaryResponse(byStatus, totalEvents, totalRegistrations);
    }

    @Transactional(readOnly = true)
    public List<EventAttendanceReportRow> eventsByAttendance() {
        return culturalEventRepository.findAllOrderByAttendeeCountDescStartsAtAsc();
    }
}
