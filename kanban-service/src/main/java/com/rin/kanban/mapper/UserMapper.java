package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CreateUserRequest;
import com.rin.kanban.dto.response.UserResponse;
import com.rin.kanban.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(CreateUserRequest request);
    User toUser(UserResponse request);
    UserResponse toUserResponse(User user);
}
