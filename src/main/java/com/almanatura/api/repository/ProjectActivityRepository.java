package com.almanatura.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.ProjectActivity;

public interface ProjectActivityRepository extends JpaRepository<ProjectActivity, Long> {

    List<ProjectActivity> findByProject_IdOrderByStartsAtAsc(long projectId);

    Optional<ProjectActivity> findByIdAndProject_Id(long id, long projectId);
}
