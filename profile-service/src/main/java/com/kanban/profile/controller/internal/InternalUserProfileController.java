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
    UserProfileResponse createUserProfile(@RequestBody ProfileCreationRequest request) {
        return userProfileService.createProfile(request);
    }

    @GetMapping("/users/my-info")
    UserProfileResponse getMyUserProfile() {
        return userProfileService.getUserProfileById();
    }

    @GetMapping("/users/{userId}")
    UserProfileResponse getUserProfile(
            @PathVariable("userId") String userId
    ) {
        return userProfileService.getUserProfileByUserId(userId);
    }
}
