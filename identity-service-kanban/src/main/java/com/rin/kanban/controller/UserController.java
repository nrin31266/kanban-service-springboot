package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.CreateUserRequest;
import com.rin.kanban.dto.response.UserInfoResponse;
import com.rin.kanban.dto.response.UserResponse;
import com.rin.kanban.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class    UserController {
    UserService userService;

    @PostMapping("/create")
    ApiResponse<UserResponse> createUser(@RequestBody @Validated CreateUserRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return  ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAll())
                .build();
    }
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUsers(@PathVariable("userId") String userId) {
        return  ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @DeleteMapping("/delete-by-email/{email}")
    ApiResponse deleteUserByUserName(@PathVariable("email") String email) {
        userService.deleteUserByEmail(email);
        return ApiResponse.builder()
                .message("Successfully delete a user with email: " + email)
                .build();
    }
    @GetMapping("/info")
    ApiResponse<UserInfoResponse> getUserInfo() {
        return ApiResponse.<UserInfoResponse>builder()
                .result(userService.getInfo())
                .build();
    }

}
