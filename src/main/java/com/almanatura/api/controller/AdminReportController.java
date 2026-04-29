package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.EventAttendanceReportRow;
import com.almanatura.api.dto.ReportsSummaryResponse;
import com.almanatura.api.service.AdminReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Tag(
        name = "Reports (admin)",
        description =
                "Aggregated metrics for cultural events and registrations. Requires an internal"
                        + " JWT (super_user or event_manager). No personal attendee data in these"
                        + " endpoints.")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/summary")
    @Operation(
            summary = "Organization summary",
            description =
                    "Returns event counts by lifecycle status plus total events and total public"
                            + " registrations (aggregates only, no PII).")
    public ReportsSummaryResponse summary() {
        return adminReportService.summary();
    }

    @GetMapping("/events/attendance")
    @Operation(
            summary = "Events ranked by registration count",
            description =
                    "Lists every cultural event with its attendee count. Ordered by count"
                            + " descending, then by start time ascending for ties.")
    public List<EventAttendanceReportRow> eventsByAttendance() {
        return adminReportService.eventsByAttendance();
    }
}
