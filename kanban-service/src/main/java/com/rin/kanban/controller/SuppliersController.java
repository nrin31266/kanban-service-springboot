package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.data.form.FormModel;
import com.rin.kanban.dto.request.ExportDataRequest;
import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.ExportSupplierDataResponse;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.service.SuppliersService;
import feign.Param;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SuppliersController {
    SuppliersService suppliersService;

    @PostMapping("create")
    public ApiResponse<SupplierResponse> create(@RequestBody @Validated SupplierRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(suppliersService.create(request))
                .message("Create supplier successfully!")
                .build();
    }

    @PostMapping("/export-data")
    public ApiResponse<List<ExportSupplierDataResponse>> exportData(
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestBody ExportDataRequest request
    ) {
        return ApiResponse.<List<ExportSupplierDataResponse>>builder()
                .result(suppliersService.exportSuppliersData(start, end, request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<SupplierResponse>> getAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        if (page != null && size != null) {
            return ApiResponse.<PageResponse<SupplierResponse>>builder()
                    .result(suppliersService.getSuppliersWithPageAndSize(page, size))
                    .build();
        }
        return ApiResponse.<PageResponse<SupplierResponse>>builder()
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
        boolean isDelete = suppliersService.removeSupplier(suppliersId);
        return ApiResponse.builder()
                .result(isDelete)
                .message(isDelete ?
                        "Deleted supplier successfully!" :
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
