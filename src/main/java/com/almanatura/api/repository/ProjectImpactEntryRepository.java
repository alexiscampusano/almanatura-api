package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.ProjectImpactEntry;

public interface ProjectImpactEntryRepository extends JpaRepository<ProjectImpactEntry, Long> {

    List<ProjectImpactEntry> findByProject_IdOrderByRecordedAtDesc(long projectId);
}
