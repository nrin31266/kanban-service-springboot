package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoriesResponse;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class CategoryController {
    CategoryService categoryService;
    @PostMapping
    public ApiResponse<CategoryResponse> addCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.addCategory(request))
                .build();
    }
    @GetMapping
    public ApiResponse<CategoriesResponse> getAllCategories() {
        return ApiResponse.<CategoriesResponse>builder()
                .result(categoryService.getAllCategories())
                .build();
    }
}
