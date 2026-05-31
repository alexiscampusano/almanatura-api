package com.almanatura.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almanatura.api.dto.CreateOutboundNotificationRequest;
import com.almanatura.api.dto.OutboundNotificationResponse;
import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.repository.OutboundNotificationRepository;

import lombok.RequiredArgsConstructor;

/** Persists outbound notification rows (audit trail only until a mailer exists). */
@Service
@RequiredArgsConstructor
public class AdminOutboundNotificationService {

    private final OutboundNotificationRepository outboundNotificationRepository;
    private final EmailSenderService emailSenderService;

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

        // Disparar el envío de forma asíncrona
        emailSenderService.sendEmailAndUpdateStatus(saved);

        return new OutboundNotificationResponse(
                saved.getId(),
                saved.getChannel(),
                saved.getRecipientHint(),
                saved.getSubject(),
                saved.getBody(),
                saved.getStatus());
    }
}
