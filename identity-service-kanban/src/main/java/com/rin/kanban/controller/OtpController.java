package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.VerifyOtpRequest;
import com.rin.kanban.dto.response.OtpResponse;
import com.rin.kanban.dto.response.VerifyOtpResponse;
import com.rin.kanban.service.OtpService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OtpController {
    OtpService otpService;

    @PostMapping("/create")
    ApiResponse<OtpResponse> createOtp(@RequestParam("userId") String userId) {
        return ApiResponse.<OtpResponse>builder()
                .result(otpService.createOtp(userId))
                .build();
    }
    @PostMapping("/verify")
    ApiResponse<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ApiResponse.<VerifyOtpResponse>builder()
                .result(otpService.verifyOtp(request))
                .build();
    }
}
