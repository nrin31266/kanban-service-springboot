package com.kanban.profile.mapper;

import com.kanban.profile.dto.request.ProfileCreationRequest;
import com.kanban.profile.dto.request.ProfileRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(ProfileCreationRequest request);
    UserProfileResponse toUserProfileResponse(Profile profile);
    void updateProfile(@MappingTarget Profile profile, ProfileRequest profileRequest);
}
