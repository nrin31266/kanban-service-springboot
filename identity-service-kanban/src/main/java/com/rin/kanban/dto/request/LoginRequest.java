package com.rin.kanban.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    String email;
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    String password;
}
