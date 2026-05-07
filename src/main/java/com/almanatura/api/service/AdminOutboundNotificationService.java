package com.almanatura.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateOutboundNotificationRequest;
import com.almanatura.api.dto.OutboundNotificationResponse;
import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.mapper.ProjectActivityMapper;
import com.almanatura.api.repository.OutboundNotificationRepository;

import lombok.RequiredArgsConstructor;

/** Persists outbound notification rows (audit trail only until a mailer exists). */
@Service
@RequiredArgsConstructor
public class AdminOutboundNotificationService {

    private final OutboundNotificationRepository outboundNotificationRepository;
    private final ProjectActivityMapper projectActivityMapper;

    @Transactional
    public OutboundNotificationResponse create(CreateOutboundNotificationRequest request) {
        OutboundNotification entity =
                OutboundNotification.builder()
                        .channel(request.channel())
                        .recipientHint(request.recipientHint())
                        .subject(request.subject())
                        .body(request.body())
                        .build();
        return projectActivityMapper.toNotificationResponse(
                outboundNotificationRepository.save(entity));
    }
}
