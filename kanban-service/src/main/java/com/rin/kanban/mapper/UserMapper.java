package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CreateUserRequest;
import com.rin.kanban.dto.response.UserResponse;
import com.rin.kanban.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest request);
    UserResponse toUserResponse(User user);
}
