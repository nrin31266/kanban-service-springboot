package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toCategoryResponse(Category category);
}
