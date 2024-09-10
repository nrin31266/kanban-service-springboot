package com.rin.kanban.service;

import com.rin.kanban.constant.PredefinedRole;
import com.rin.kanban.dto.request.CreateUserRequest;
import com.rin.kanban.dto.response.UserResponse;
import com.rin.kanban.entity.Role;
import com.rin.kanban.entity.User;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.UserMapper;
import com.rin.kanban.repository.RoleRepository;
import com.rin.kanban.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(CreateUserRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        User user = userMapper.toUser(request);
        user.setRoles(getRoles(List.of(PredefinedRole.USER.name())));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }
    @PostAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }
    @PostAuthorize("returnObject.id == authentication.name || hasRole('ADMIN')")
    public UserResponse getUser(String userID) {
        return userMapper.toUserResponse(
                userRepository.findById(userID).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }
    private HashSet<Role> getRoles(List<String> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return null;
        }
        List<Role> roles = roleRepository.findAllById(requestedRoles);
        return new HashSet<>(roles);
    }
}
