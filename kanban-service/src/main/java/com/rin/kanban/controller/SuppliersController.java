package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.data.form.FormModel;
import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.service.SuppliersService;
import feign.Param;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SuppliersController {
    SuppliersService suppliersService;

    @PostMapping("create")
    public ApiResponse<SupplierResponse> create(@RequestBody @Validated SupplierRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(suppliersService.create(request))
                .message("Create supplier successfully!")
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<SupplierResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size
    ) {
        return ApiResponse.<PageResponse<SupplierResponse>>builder()
                .result(suppliersService.getAll(page, size))
                .build();
    }
    @PutMapping("/{suppliersId}")
    public ApiResponse<SupplierResponse> update(
            @PathVariable("suppliersId") String suppliersId,
            @RequestBody @Validated SupplierRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(suppliersService.updateSuppliers(request, suppliersId))
                .message("Update supplier successfully!")
                .build();
    }
    @DeleteMapping
    public ApiResponse delete(@Param String suppliersId) {
        boolean isDelete = suppliersService.removeSupplier(suppliersId);
        return ApiResponse.builder()
                .result(isDelete)
                .message(isDelete?
                        "Deleted supplier successfully!":
                        "Cannot deleted supplier")
                .build();
    }
    @GetMapping("/form")
    public ApiResponse<FormModel> form() {
        return ApiResponse.<FormModel>builder()
                .result(suppliersService.getForm())
                .build();
    }

}
