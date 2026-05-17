package com.almanatura.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almanatura.api.entity.OutboundNotification;

public interface OutboundNotificationRepository extends JpaRepository<OutboundNotification, Long> {}
