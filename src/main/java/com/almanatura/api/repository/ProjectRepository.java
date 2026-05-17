package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.almanatura.api.dto.ProjectApplicationReportRow;
import com.almanatura.api.entity.Project;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByTitleIgnoreCase(String title);

    long countByStatus(ProjectStatus status);

    List<Project> findByStatusOrderByStartsAtAsc(ProjectStatus status);

    Page<Project> findByStatusOrderByStartsAtAsc(ProjectStatus status, Pageable pageable);

    List<Project> findByStatusAndPillarOrderByStartsAtAsc(
            ProjectStatus status, ProjectPillar pillar);

    Page<Project> findByStatusAndPillarOrderByStartsAtAsc(
            ProjectStatus status, ProjectPillar pillar, Pageable pageable);

    Optional<Project> findByIdAndStatus(long id, ProjectStatus status);

    boolean existsByIdAndStatus(long id, ProjectStatus status);

    List<Project> findAllByOrderByStartsAtAsc();

    @Query(
            """
            SELECT new com.almanatura.api.dto.ProjectApplicationReportRow(
                p.id, p.title, p.startsAt, p.pillar, p.status, COUNT(app.id))
            FROM Project p LEFT JOIN ProjectApplication app ON app.project.id = p.id
            GROUP BY p.id, p.title, p.startsAt, p.pillar, p.status
            ORDER BY COUNT(app.id) DESC, p.startsAt ASC
            """)
    List<ProjectApplicationReportRow> findAllOrderByApplicationCountDescStartsAtAsc();
}
