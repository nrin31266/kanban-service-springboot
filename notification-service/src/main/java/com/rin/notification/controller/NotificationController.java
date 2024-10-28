package com.rin.notification.controller;

import com.rin.envent.dto.NotificationEvent;
import com.rin.notification.dto.request.EmailRecipient;
import com.rin.notification.dto.request.EmailRequest;
import com.rin.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    EmailService emailService;

    @KafkaListener(topics = "onboard-successful", groupId = "notification-group")
    public void listen(String message) {
        log.info("Message received: {}", message);
    }

    @KafkaListener(topics = "notification-delivery", groupId = "notification-group")
    public void listenNotificationDelivery(NotificationEvent request) {
        log.info("Message received: {}", request);
        emailService.sendWelcomeEmail(request);
    }

    @KafkaListener(topics = "notification-otp-email")
    public void listenNotificationVerifyOtpCodeWithEmail(NotificationEvent request) {
        log.info("Message received: {}", request);
        emailService.sendOtpCode(request);
    }

}
