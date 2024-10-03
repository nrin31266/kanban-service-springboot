package com.rin.kanban.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CategoryTreeResponse {
    String value;
    String title;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    List<CategoryTreeResponse> children;
}
