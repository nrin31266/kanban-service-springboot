package com.kanban.profile.controller;


import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.request.ProfileCreationRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<UserProfileResponse> createUserProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }

    @GetMapping("internal/users/my-info")
    ApiResponse<UserProfileResponse> getUserProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileById())
                .build();
    }
}
