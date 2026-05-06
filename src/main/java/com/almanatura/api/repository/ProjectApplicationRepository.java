package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.almanatura.api.entity.ProjectApplication;
import com.almanatura.api.enums.ApplicationStatus;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    boolean existsByProject_IdAndEmailIgnoreCase(long projectId, String email);

    long countByStatus(ApplicationStatus status);

    long countByProject_Id(long projectId);

    @Query(
            """
            SELECT a FROM ProjectApplication a
            WHERE (:projectId IS NULL OR a.project.id = :projectId)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.createdAt DESC
            """)
    List<ProjectApplication> search(
            @Param("projectId") Long projectId, @Param("status") ApplicationStatus status);

    List<ProjectApplication> findByProject_IdOrderByCreatedAtAsc(long projectId);

    List<ProjectApplication> findByStatusOrderByCreatedAtAsc(ApplicationStatus status);
}
