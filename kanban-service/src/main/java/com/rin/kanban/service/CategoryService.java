package com.rin.kanban.service;

import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoriesResponse;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CategoryMapper;
import com.rin.kanban.repository.CategoryRepository;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    public CategoryResponse addCategory(CategoryRequest request) {
        if (categoryRepository.findByParentIdAndSlug(request.getParentId(), request.getSlug()).isPresent()){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        return categoryMapper.toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(request)));
    }

    public CategoriesResponse getAllCategories() {
        List<CategoryResponse> categoriesParent = categoryRepository.findAllByParentIdIsNull().stream().map(categoryMapper::toCategoryResponse).toList();
        List<CategoryResponse> categoriesChildren = categoryRepository.findAllByParentIdNotNull().stream().map(categoryMapper::toCategoryResponse).toList();
        return CategoriesResponse.builder()
                .categoriesParent(categoriesParent)
                .categoriesChildren(categoriesChildren)
                .build();
    }
}
