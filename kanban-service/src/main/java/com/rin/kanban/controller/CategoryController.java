package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> addCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.addCategory(request))
                .build();
    }

    @GetMapping("/get")
    public ApiResponse<CategoriesResponse> getCategories() {
        return ApiResponse.<CategoriesResponse>builder()
                .result(categoryService.getCategories())
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> getAllCategories(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        if (page != null && size != null) {
            return ApiResponse.<PageResponse<CategoryResponse>>builder()
                    .result(categoryService.getCategoriesByPageAndSize(page, size))
                    .build();
        }
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .result(
                        PageResponse.<CategoryResponse>builder()
                                .totalPages(1)
                                .currentPage(1)
                                .totalPages(categories.size())
                                .pageSize(categories.size())
                                .data(categories)
                                .build()
                )
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
