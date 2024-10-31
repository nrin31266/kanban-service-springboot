package com.rin.kanban.service;

import com.rin.envent.dto.NotificationEvent;
import com.rin.kanban.dto.request.CreateUserRequest;
import com.rin.kanban.dto.response.OtpResponse;
import com.rin.kanban.dto.response.UserInfoResponse;
import com.rin.kanban.dto.response.UserResponse;
import com.rin.kanban.entity.Role;
import com.rin.kanban.entity.User;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.UserMapper;
import com.rin.kanban.repository.OtpRepository;
import com.rin.kanban.repository.RoleRepository;
import com.rin.kanban.repository.UserRepository;
import com.rin.kanban.utils.OtpGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    KafkaTemplate<String, Object> kafkaTemplate;
    OtpGenerator otpGenerator;
    OtpService otpService;
    OtpRepository otpRepository;


    public UserResponse createUser(CreateUserRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        User user = userMapper.toUser(request);
        var userRole = new HashSet<Role>();
        userRole.add(roleRepository.findById("USER")
                .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_FOUND)));

        user.setRoles(userRole);

        if ((request.getPassword() != null)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        try {
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        otpService.sendEmailVerifyByUser(user);
        return userMapper.toUserResponse(user);
    }

    public UserInfoResponse getInfo(){
        var context = SecurityContextHolder.getContext();
        var userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserInfoResponse(user);

    };
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
    @PostAuthorize("hasRole('ADMIN')")
    public void deleteUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.deleteById(user.getId());
    }
}
