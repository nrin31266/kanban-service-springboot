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
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    String name;
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    @Email(message = "INVALID_EMAIL")
    String email;
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    String type;
}
