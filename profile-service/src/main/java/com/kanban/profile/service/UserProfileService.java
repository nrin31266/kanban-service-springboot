package com.kanban.profile.service;

import com.kanban.profile.dto.request.ProfileCreationRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.entity.Profile;
import com.kanban.profile.exception.AppException;
import com.kanban.profile.exception.ErrorCode;
import com.kanban.profile.mapper.ProfileMapper;
import com.kanban.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profile = profileMapper.toProfile(request);
        profileRepository.save(profile);
        return profileMapper.toUserProfileResponse(profile);

    }

    public UserProfileResponse getUserProfileById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()->new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return profileMapper.toUserProfileResponse(profile);
    }
}
