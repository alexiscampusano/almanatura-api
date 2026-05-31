package com.almanatura.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.almanatura.api.entity.OutboundNotification;
import com.almanatura.api.enums.OutboundNotificationStatus;
import com.almanatura.api.repository.OutboundNotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final OutboundNotificationRepository outboundNotificationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.mail.from-address:comunicaciones@almanatura.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:AlmaNatura}")
    private String fromName;

    @Value("${app.mail.brevo.api-key:}")
    private String brevoApiKey;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    @Async
    public void sendEmailAndUpdateStatus(OutboundNotification notification) {
        if (notification.getRecipientHint() == null || notification.getRecipientHint().isEmpty()) {
            updateStatus(notification, OutboundNotificationStatus.FAILED);
            return;
        }

        if (brevoApiKey == null || brevoApiKey.isEmpty()) {
            log.error("Brevo API Key is missing. Cannot send email to {}", notification.getRecipientHint());
            updateStatus(notification, OutboundNotificationStatus.FAILED);
            return;
        }

        try {
            log.info("Preparing to send email to {} via Brevo", notification.getRecipientHint());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, Object> body = Map.of(
                    "sender", Map.of("name", fromName, "email", fromAddress),
                    "to", List.of(Map.of("email", notification.getRecipientHint())),
                    "subject", notification.getSubject(),
                    "htmlContent", notification.getBody());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email successfully sent to {}", notification.getRecipientHint());
                updateStatus(notification, OutboundNotificationStatus.SENT);
            } else {
                log.error("Failed to send email to {}. Brevo response: {}", notification.getRecipientHint(), response.getBody());
                updateStatus(notification, OutboundNotificationStatus.FAILED);
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}", notification.getRecipientHint(), e);
            updateStatus(notification, OutboundNotificationStatus.FAILED);
        }
    }

    private void updateStatus(
            OutboundNotification notification, OutboundNotificationStatus status) {
        notification.setStatus(status);
        outboundNotificationRepository.save(notification);
    }
}
