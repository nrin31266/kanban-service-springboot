package com.kanban.profile.controller;

import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.service.UserProfileService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
