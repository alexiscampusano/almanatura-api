package com.almanatura.api.dto;

import java.util.List;

/**
 * Dashboard-style aggregates for internal staff: event totals by status and overall registration
 * volume (no personal data).
 */
public record ReportsSummaryResponse(
        List<EventStatusCount> eventsByStatus, long totalEvents, long totalRegistrations) {}
