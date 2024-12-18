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
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SelectCategoryTreeResponse {
    String value;
    String key;
    String label;
    String title;
    List<SelectCategoryTreeResponse> children;
}
