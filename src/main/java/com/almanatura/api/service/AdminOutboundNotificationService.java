package com.almanatura.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateOutboundNotificationRequest;
import com.almanatura.api.dto.OutboundNotificationResponse;
import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.repository.OutboundNotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOutboundNotificationService {

    private final OutboundNotificationRepository outboundNotificationRepository;

    @Transactional
    public OutboundNotificationResponse create(CreateOutboundNotificationRequest request) {
        OutboundNotification entity =
                OutboundNotification.builder()
                        .channel(request.channel())
                        .recipientHint(request.recipientHint())
                        .subject(request.subject())
                        .body(request.body())
                        .build();
        OutboundNotification saved = outboundNotificationRepository.save(entity);
        return new OutboundNotificationResponse(
                saved.getId(),
                saved.getChannel(),
                saved.getRecipientHint(),
                saved.getSubject(),
                saved.getBody(),
                saved.getStatus());
    }
}
