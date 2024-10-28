package com.rin.notification.service;

import com.rin.envent.dto.NotificationEvent;
import com.rin.notification.dto.request.EmailRecipient;
import com.rin.notification.dto.request.EmailRequest;
import com.rin.notification.dto.request.EmailSender;
import com.rin.notification.dto.response.EmailResponse;
import com.rin.notification.exception.AppException;
import com.rin.notification.exception.ErrorCode;
import com.rin.notification.respository.httpclient.EmailClient;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    EmailClient emailClient;

    @NonFinal
    @Value("${app.api-key}")
    String apiKey;

    @NonFinal
    @Value("${app.email}")
    String email;

    @NonFinal
    EmailSender emailSender;

    @PostConstruct
    public void init() {
        emailSender = EmailSender.builder()
                .email(email)
                .name("Nguyễn Văn Rin")
                .build();
    }

    public EmailResponse sendEmail(EmailRequest emailRequest) {
        log.info("Sending email notification");
        log.info(emailRequest.toString());
        try {
            EmailResponse emailResponse = emailClient.sendEmail(apiKey, emailRequest);
            log.info("Email sent successfully");
            return emailResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

    public EmailResponse sendOtpCode(NotificationEvent request) {
        String recipientName = (String) request.getParam().getOrDefault("name", "N/A");
        String otpCode = (String) request.getParam().getOrDefault("otpCode", null);
        if(otpCode == null) {
            log.info("OTP code is null");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(emailSender)
                .to(List.of(EmailRecipient.builder()
                        .name(recipientName)
                        .email(request.getRecipient())
                        .build()))
                .htmlContent(request.getBody() + ": " + otpCode)
                .subject(request.getSubject())
                .build();
        return sendEmail(emailRequest);
    }

    public EmailResponse sendWelcomeEmail(NotificationEvent request) {
        String recipientName = (String) request.getParam().getOrDefault("name", null);
        if(recipientName == null) {
            log.info("Recipient name is null");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(emailSender)
                .to(List.of(EmailRecipient.builder()
                        .name(recipientName)
                        .email(request.getRecipient())
                        .build()))
                .htmlContent(request.getBody())
                .subject(request.getSubject())
                .build();
        return sendEmail(emailRequest);
    }


}
