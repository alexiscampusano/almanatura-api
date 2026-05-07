package com.almanatura.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almanatura.api.dto.ProjectApplicationReportRow;
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
        description = "Aggregated metrics for projects and applications. No applicant PII.")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/summary")
    @Operation(
            summary = "Organization summary",
            description =
                    "Project counts by lifecycle status, application and planning rollups "
                            + "(activities, participations, impact entries, outbound notification "
                            + "records). No personal applicant data.")
    public ReportsSummaryResponse summary() {
        return adminReportService.summary();
    }

    @GetMapping("/projects/applications")
    @Operation(
            summary = "Projects ranked by application count",
            description =
                    "Each project with application count, ordered by count descending then start"
                            + " time.")
    public List<ProjectApplicationReportRow> projectsByApplicationCount() {
        return adminReportService.projectsByApplicationCount();
    }
}
