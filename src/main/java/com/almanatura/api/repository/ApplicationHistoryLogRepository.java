package com.almanatura.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.ApplicationHistoryLog;

public interface ApplicationHistoryLogRepository
        extends JpaRepository<ApplicationHistoryLog, Long> {
    List<ApplicationHistoryLog> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
