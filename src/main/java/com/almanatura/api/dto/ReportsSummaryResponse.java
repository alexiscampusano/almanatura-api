package com.almanatura.api.dto;

import java.util.List;

public record ReportsSummaryResponse(
        List<ProjectStatusCount> projectsByStatus,
        long totalProjects,
        long totalApplications,
        long totalProjectActivities,
        long totalActivityParticipations,
        long totalImpactEntries,
        long totalOutboundNotifications) {}
