package com.almanatura.api.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.ProjectApplicationReportRow;
import com.almanatura.api.dto.ProjectStatusCount;
import com.almanatura.api.dto.ReportsSummaryResponse;
import com.almanatura.api.enums.ProjectStatus;
import com.almanatura.api.repository.ProjectApplicationRepository;
import com.almanatura.api.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    @Transactional(readOnly = true)
    public ReportsSummaryResponse summary() {
        List<ProjectStatusCount> byStatus =
                Arrays.stream(ProjectStatus.values())
                        .map(
                                status ->
                                        new ProjectStatusCount(
                                                status, projectRepository.countByStatus(status)))
                        .sorted(Comparator.comparing(e -> e.status().name()))
                        .toList();
        long totalProjects = projectRepository.count();
        long totalApplications = projectApplicationRepository.count();
        return new ReportsSummaryResponse(byStatus, totalProjects, totalApplications);
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicationReportRow> projectsByApplicationCount() {
        return projectRepository.findAllOrderByApplicationCountDescStartsAtAsc();
    }
}
