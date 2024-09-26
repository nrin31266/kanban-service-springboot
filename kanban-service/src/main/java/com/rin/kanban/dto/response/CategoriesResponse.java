package com.rin.kanban.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoriesResponse {
    List<CategoryResponse> categoriesParent;
    List<CategoryResponse> categoriesChildren;
}
