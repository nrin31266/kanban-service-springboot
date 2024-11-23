package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.CategoryRequest;
import com.rin.kanban.dto.response.CategoryResponse;
import com.rin.kanban.dto.response.CategoryTableResponse;
import com.rin.kanban.dto.response.SelectCategoryTreeResponse;
import com.rin.kanban.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> addCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.addCategory(request))
                .build();
    }

    @GetMapping("/get-tree")
    public ApiResponse<List<SelectCategoryTreeResponse>> getCategories() {
        return ApiResponse.<List<SelectCategoryTreeResponse>>builder()
                .result(categoryService.getAllCategoriesTree(false))
                .build();
    }

    @GetMapping("/get-menu-tree")
    public ApiResponse<List<SelectCategoryTreeResponse>> getMenuTreeCategories() {
        return ApiResponse.<List<SelectCategoryTreeResponse>>builder()
                .result(categoryService.getAllCategoriesTree(true))
                .build();
    }


    @GetMapping("/table-data")
    public ApiResponse<List<CategoryTableResponse>> getCategoriesTableData() {
        return ApiResponse.<List<CategoryTableResponse>>builder()
                .result(categoryService.getCategoriesTableData())
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories(
    ) {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategories())
                .build();

    }

    @GetMapping("/root")
    public ApiResponse<List<CategoryResponse>> getRootCategory() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getRootCategories())
                .build();
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Boolean> deleteCategory(@PathVariable("categoryId") String categoryId) {
        Boolean valid = categoryService.deleteCategory(categoryId);
        return ApiResponse.<Boolean>builder()
                .result(valid)
                .build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@RequestBody CategoryRequest request, @PathVariable("categoryId") String categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(request, categoryId))
                .build();
    }
}
