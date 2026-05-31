package com.almanatura.api.service;

import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.enums.NotificationStatus;
import com.almanatura.api.repository.OutboundNotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final OutboundNotificationRepository outboundNotificationRepository;

    @Value("${app.mail.from-address:comunicaciones@almanatura.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:AlmaNatura}")
    private String fromName;

    @Async
    public void sendEmailAndUpdateStatus(OutboundNotification notification) {
        if (notification.getRecipientHint() == null || notification.getRecipientHint().isEmpty()) {
            updateStatus(notification, NotificationStatus.FAILED);
            return;
        }

        try {
            log.info("Preparing to send email to {}", notification.getRecipientHint());
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(notification.getRecipientHint());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getBody(), true);

            javaMailSender.send(message);
            log.info("Email successfully sent to {}", notification.getRecipientHint());
            updateStatus(notification, NotificationStatus.SENT);
        } catch (Exception e) {
            log.error("Failed to send email to {}", notification.getRecipientHint(), e);
            updateStatus(notification, NotificationStatus.FAILED);
        }
    }

    private void updateStatus(OutboundNotification notification, NotificationStatus status) {
        notification.setStatus(status);
        outboundNotificationRepository.save(notification);
    }
}
