package com.rin.kanban.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuppliersRequest {
    String photoUrl;
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    String name;
    String product;
    Set<String> categories;
    BigDecimal price;
    @NotNull(message = "INVALID_FIELD")
    @NotEmpty(message = "INVALID_FIELD")
    @NotBlank(message = "INVALID_FIELD")
    String contact;
    boolean talking;
}
