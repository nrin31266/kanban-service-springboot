package com.rin.kanban.controller.outbound;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.response.AuthenticationResponse;
import com.rin.kanban.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/outbound")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OutboundController {
    AuthenticationService authenticationService;

    @PostMapping("/google-login")
    public ApiResponse<AuthenticationResponse> loginWithGoogle(@RequestParam("code") String code) {
        var result = authenticationService.outboundLogin(code);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
