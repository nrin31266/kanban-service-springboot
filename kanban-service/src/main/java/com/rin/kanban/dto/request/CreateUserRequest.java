package com.rin.kanban.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CreateUserRequest {
    @NotNull
    @NotEmpty
    @NotBlank
    String name;
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    String email;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
}
