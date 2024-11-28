package com.kanban.profile.controller.internal;


import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.request.ProfileCreationRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/internal")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/users")
    ApiResponse<UserProfileResponse> createUserProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }

    @GetMapping("/users/my-info")
    ApiResponse<UserProfileResponse> getMyUserProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileById())
                .build();
    }

    @GetMapping("/users/{userId}")
    ApiResponse<UserProfileResponse> getUserProfile(
            @PathVariable("userId") String userId
    ) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileByUserId(userId))
                .build();
    }
}
