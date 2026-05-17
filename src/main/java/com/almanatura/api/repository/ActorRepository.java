package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.almanatura.api.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    boolean existsByFullNameIgnoreCase(String fullName);

    @Query("SELECT DISTINCT a FROM Actor a " +
           "JOIN ProjectApplication pa ON pa.actor.id = a.id " +
           "JOIN Project p ON pa.project.id = p.id " +
           "WHERE p.pillar = :pillar")
    List<Actor> findByProjectPillar(@Param("pillar") com.almanatura.api.enums.ProjectPillar pillar);
}
