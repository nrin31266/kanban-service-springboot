package com.rin.kanban.mapper;

import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.dto.response.CategoryTableResponse;
import com.rin.kanban.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toCategoryResponse(Category category);
    void categoryUpdate(@MappingTarget Category category, CategoryRequest request);
    @Mapping(target = "key", source = "id")
    @Mapping(target = "children", ignore = true)
    CategoryTableResponse toCategoryTableResponse(Category category);
}
