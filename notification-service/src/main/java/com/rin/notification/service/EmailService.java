package com.rin.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.envent.dto.NotificationEvent;
import com.rin.notification.dto.noname.Order;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    EmailClient emailClient;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper jacksonObjectMapper;

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

    public EmailResponse sendOtpCode(NotificationEvent request) throws IOException {
        String recipientName = (String) request.getParam().getOrDefault("name", null);
        String otpCode = (String) request.getParam().getOrDefault("otpCode", null);
        if (otpCode == null || recipientName == null) {
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
        String emailTemplate = readTemplateFromResources("templates/verification.html");
        String emailBody = emailTemplate
                .replace("{{userName}}", recipientName)
                .replace("{{otpCode}}", otpCode);
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(emailSender)
                .to(List.of(EmailRecipient.builder()
                        .name(recipientName)
                        .email(request.getRecipient())
                        .build()))
                .htmlContent(emailBody)
                .subject(request.getSubject())
                .build();
        return sendEmail(emailRequest);
    }

    public EmailResponse sendWelcomeEmail(NotificationEvent request) throws IOException {
        String recipientName = (String) request.getParam().getOrDefault("name", null);
        String otpCode = (String) request.getParam().getOrDefault("otpCode", null);
        String type = (String) request.getParam().getOrDefault("type", "default");

        if (recipientName == null || (otpCode == null && !"Google".equals(type))) {
            log.info("Recipient name is null or OTP code is required but missing");
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }

        String emailTemplate = readTemplateFromResources("templates/welcome_email.html");
        String emailBody;

        if ("Google".equals(type)) {
            emailBody = emailTemplate
                    .replace("{{userName}}", recipientName)
                    .replace("{{otpSection}}",
                            "<a href=\"http://localhost:3004\" class=\"cta-button\">Get Started</a>"
                    );
        } else {
            emailBody = emailTemplate
                    .replace("{{userName}}", recipientName)
                    .replace("{{otpSection}}",
                            "<p>Use the following code to verify your account:</p>" +
                                    "<div class=\"cta-button\">{{otpCode}}</div>"
                    )
                    .replace("{{otpCode}}", otpCode);
        }

        EmailRequest emailRequest = EmailRequest.builder()
                .sender(emailSender)
                .to(List.of(EmailRecipient.builder()
                        .name(recipientName)
                        .email(request.getRecipient())
                        .build()))
                .htmlContent(emailBody)
                .subject(request.getSubject())
                .build();

        return sendEmail(emailRequest);
    }



    private String readTemplateFromResources(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Template not found: " + resourcePath);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public EmailResponse sendOrderEmail(NotificationEvent request) {
        try {
            // Lấy dữ liệu từ param và chuyển đổi sang đối tượng Order
            Map<String, Object> param = request.getParam();
            Map<String, Object> orderMap = (Map<String, Object>) param.get("order");
            Order order = jacksonObjectMapper.convertValue(orderMap, Order.class);

            // Thiết lập context cho template
            Context context = new Context();
            context.setVariable("order", order);

            // Render HTML template thành chuỗi
            String htmlContent = templateEngine.process("order-email-template", context);

            // Tạo EmailRequest để gửi email
            EmailRequest emailRequest = EmailRequest.builder()
                    .sender(emailSender)
                    .to(List.of(EmailRecipient.builder()
                            .name(order.getCustomerName())
                            .email(request.getRecipient())
                            .build()))
                    .htmlContent(htmlContent)
                    .subject(request.getSubject())
                    .build();

            // Gửi email
            return sendEmail(emailRequest);

        } catch (Exception e) {
            log.error("Failed to send order email", e);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
