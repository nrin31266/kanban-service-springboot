package com.rin.notification.controller;

import com.rin.envent.dto.NotificationEvent;
import com.rin.notification.dto.ApiResponse;
import com.rin.notification.dto.request.EmailRequest;
import com.rin.notification.dto.request.SendEmailRequest;
import com.rin.notification.dto.response.EmailResponse;
import com.rin.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/email")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    EmailService emailService;
    @PostMapping("/send")
    public ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", request.getSubject());
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendWelcomeEmail(
                        NotificationEvent.builder()
                                .body(request.getBody())
                                .subject(request.getSubject())
                                .param(params)
                                .recipient(request.getTo())
                                .build()
                ))
                .build();

    }
}
