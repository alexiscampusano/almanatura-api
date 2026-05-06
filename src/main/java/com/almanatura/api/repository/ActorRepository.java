package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.almanatura.api.entity.Actor;
import com.almanatura.api.enums.ApplicationStatus;
import com.almanatura.api.enums.ProjectPillar;
import com.almanatura.api.enums.ProjectStatus;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    @Query(
            """
            SELECT DISTINCT a FROM Actor a
            INNER JOIN ProjectApplication app ON app.actor.id = a.id
            INNER JOIN app.project p
            WHERE app.status = :registeredStatus
              AND p.status = :publishedStatus
              AND (:pillar IS NULL OR p.pillar = :pillar)
            ORDER BY a.fullName ASC
            """)
    List<Actor> findDirectoryActors(
            @Param("registeredStatus") ApplicationStatus registeredStatus,
            @Param("publishedStatus") ProjectStatus publishedStatus,
            @Param("pillar") ProjectPillar pillar);
}
