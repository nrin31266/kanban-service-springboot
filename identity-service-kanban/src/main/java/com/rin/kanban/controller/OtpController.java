package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.VerifyOtpRequest;
import com.rin.kanban.dto.response.OtpResponse;
import com.rin.kanban.dto.response.VerifyOtpResponse;
import com.rin.kanban.service.OtpService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OtpController {
    OtpService otpService;

    @PostMapping("/create")
    ApiResponse<OtpResponse> createOtp() {
        return ApiResponse.<OtpResponse>builder()
                .result(otpService.createOtp())
                .build();
    }

    @PostMapping("/verify")
    ApiResponse<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ApiResponse.<VerifyOtpResponse>builder()
                .result(otpService.verifyOtp(request))
                .build();
    }

    @PostMapping("/user-verify")
    ApiResponse<VerifyOtpResponse> userVerify(@RequestBody VerifyOtpRequest request) {
        return ApiResponse.<VerifyOtpResponse>builder()
                .result(otpService.userVerify(request))
                .build();
    }

    @PostMapping("/send-email-verify/{userId}")
    ApiResponse reSendEmailVerify(@PathVariable("userId") String userId) {
        otpService.sendEmailVerify(userId);
        return ApiResponse.builder()
                .message("ok")
                .build();
    }
}
