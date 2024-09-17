package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.service.SuppliersService;
import feign.Param;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<List<SupplierResponse>> getAll() {
        return ApiResponse.<List<SupplierResponse>>builder()
                .result(suppliersService.getAll())
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
        return ApiResponse.builder()
                .message((suppliersService.removeSupplier(suppliersId))?
                        "Successfully deleted supplier successfully!":
                        "Cannot deleted supplier")
                .build();
    }

}
