package com.kanban.profile.mapper;

import com.kanban.profile.dto.request.ProfileCreationRequest;
import com.kanban.profile.dto.response.UserProfileResponse;
import com.kanban.profile.entity.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(ProfileCreationRequest request);
    UserProfileResponse toUserProfileResponse(Profile profile);
}
