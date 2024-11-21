package com.kanban.profile.controller;

import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.request.ChangeAvatarRequest;
import com.kanban.profile.dto.request.ProfileRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.service.UserProfileService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/my-info")
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileById())
                .build();
    }

    @PutMapping
    public ApiResponse<UserProfileResponse> updateProfile(@RequestBody ProfileRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateProfile(request))
                .build();
    }
    @PutMapping("/avatar")
    public ApiResponse<UserProfileResponse> changeAvatar(@RequestBody ChangeAvatarRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.changeAvatar(request))
                .build();
    }


}
