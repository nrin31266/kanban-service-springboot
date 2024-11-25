package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.dto.response.CategoryTableResponse;
import com.rin.kanban.dto.response.SelectCategoryTreeResponse;
import com.rin.kanban.entity.Category;
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


    public CategoryResponse addCategory(CategoryRequest request) {
        // Save category to database
        Category category = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public List<SelectCategoryTreeResponse> getAllCategoriesTree(boolean isMenu) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        List<Category> categories = categoryRepository.findAll(sort);
        Map<String, SelectCategoryTreeResponse> map = new HashMap<>();
        for (Category category : categories) {
            if (isMenu) {
                map.put(category.getId(),
                        SelectCategoryTreeResponse.builder()
                                .key(category.getId())
                                .label(category.getName())
                                .build());
            } else {
                map.put(category.getId(),
                        SelectCategoryTreeResponse.builder()
                                .value(category.getId())
                                .title(category.getName())
                                .build());
            }
        }
        List<SelectCategoryTreeResponse> categoriesTree = new ArrayList<>();
        for (Category category : categories) {
            SelectCategoryTreeResponse item = map.get(category.getId());
            if (category.getParentId() == null) {
                categoriesTree.add(item);
            } else {
                SelectCategoryTreeResponse parent = map.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
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
            CategoryTableResponse item = map.get(category.getId());
            if (category.getParentId() == null) {
                categoriesTable.add(item);
            } else {
                CategoryTableResponse parent = map.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(item);
                }
            }
        });
        return categoriesTable;
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).toList();
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
