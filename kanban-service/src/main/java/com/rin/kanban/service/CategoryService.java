package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.dto.response.CategoryTableResponse;
import com.rin.kanban.dto.response.CategoryTreeResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.CategoryMapper;
import com.rin.kanban.mapper.ProductMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.ProductRepository;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    ProductRepository productRepository;
    ProductMapper productMapper;

    public CategoryResponse addCategory(CategoryRequest request) {
        if (categoryRepository.findByParentIdAndSlug(request.getParentId(), request.getSlug()).isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        return categoryMapper.toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(request)));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public List<CategoryTreeResponse> getAllCategoriesTree() {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<Category> categories = categoryRepository.findAll(sort);
        Map<String, CategoryTreeResponse> map = new HashMap<>();
        for (Category category : categories) {
            CategoryTreeResponse response = CategoryTreeResponse.builder()
                    .value(category.getId())
                    .title(category.getName())
                    .build();
            map.put(category.getId(), response);
        }
        List<CategoryTreeResponse> categoriesTree = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId() == null) {
                categoriesTree.add(map.get(category.getId()));
            } else {
                CategoryTreeResponse parent = map.get(category.getParentId());
                CategoryTreeResponse item = map.get(category.getId());
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>(Collections.singletonList(item)));
                } else {
                    parent.getChildren().add(item);
                }
            }
        }
        return categoriesTree;
    }

    public List<CategoryTableResponse> getCategoriesTableData() {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<Category> categories = categoryRepository.findAll(sort);

        Map<String, CategoryTableResponse> map = new HashMap<>();
        categories.forEach(category -> {
            CategoryTableResponse categoryTreeResponse = categoryMapper.toCategoryTableResponse(category);
            map.put(category.getId(), categoryTreeResponse);
        });
        List<CategoryTableResponse> categoriesTable = new ArrayList<>();
        categories.forEach(category -> {
            if (category.getParentId() == null) {
                categoriesTable.add(map.get(category.getId()));
            } else {
                CategoryTableResponse parent = map.get(category.getParentId());
                CategoryTableResponse item = map.get(category.getId());
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>(Collections.singletonList(item)));
                } else {
                    parent.getChildren().add(item);
                }
            }
        });
        return categoriesTable;
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

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findAllRootCategories().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    @Transactional
    public Boolean deleteCategory(String categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryRepository.deleteById(categoryId);
            List<Category> categoriesChildren = categoryRepository.findAllByParentId(categoryId);
            categoriesChildren.forEach(item -> item.setParentId(category.getParentId() != null ? category.getParentId() : null));
            categoryRepository.saveAll(categoriesChildren);

            return true;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }


    public CategoryResponse updateCategory(CategoryRequest request, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryMapper.categoryUpdate(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }
}
