package com.rin.kanban.controller;

import com.nimbusds.jose.JOSEException;
import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.IntrospectRequest;
import com.rin.kanban.dto.request.LoginRequest;
import com.rin.kanban.dto.request.LogoutRequest;
import com.rin.kanban.dto.request.RefreshRequest;
import com.rin.kanban.dto.response.AuthenticationResponse;
import com.rin.kanban.dto.response.RefreshTokenResponse;
import com.rin.kanban.dto.response.IntrospectResponse;
import com.rin.kanban.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody @Validated LoginRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticated(request))
                .build();
    }
    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);

        return ApiResponse.<RefreshTokenResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result).build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponse.<Void>builder().build();
    }
}
