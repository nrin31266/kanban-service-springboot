package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoriesResponse;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CategoryMapper;
import com.rin.kanban.repository.CategoryRepository;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    public CategoryResponse addCategory(CategoryRequest request) {
        if (categoryRepository.findByParentIdAndSlug(request.getParentId(), request.getSlug()).isPresent()){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        return categoryMapper.toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(request)));
    }

    public CategoriesResponse getCategories() {
        List<CategoryResponse> categoriesParent = categoryRepository.findAllByParentIdIsNull().stream().map(categoryMapper::toCategoryResponse).toList();
        List<CategoryResponse> categoriesChildren = categoryRepository.findAllByParentIdNotNull().stream().map(categoryMapper::toCategoryResponse).toList();
        return CategoriesResponse.builder()
                .categoriesParent(categoriesParent)
                .categoriesChildren(categoriesChildren)
                .build();
    }
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public PageResponse<CategoryResponse> getCategoriesByPageAndSize(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Category> pageData = categoryRepository.findAll(pageable);

        return PageResponse.<CategoryResponse>builder()
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(categoryMapper::toCategoryResponse).toList())
                .build();
    }

    public Boolean deleteCategory(String categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public CategoryResponse updateCategory(CategoryRequest request, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryMapper.categoryUpdate(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }
}
