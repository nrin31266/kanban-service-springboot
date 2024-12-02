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

import java.io.IOException;

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
    public void listenNotificationDelivery(NotificationEvent request) throws IOException {
        emailService.sendWelcomeEmail(request);
    }

    @KafkaListener(topics = "notification-otp-email")
    public void listenNotificationVerifyOtpCodeWithEmail(NotificationEvent request) throws IOException {
        emailService.sendOtpCode(request);
    }

    @KafkaListener(topics = "notification-order")
    public void listenNotificationOrder(NotificationEvent request) {
        emailService.sendOrderEmail(request);
    }


}
