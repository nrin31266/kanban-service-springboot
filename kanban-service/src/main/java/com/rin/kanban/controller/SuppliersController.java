package com.rin.kanban.controller;

import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.request.SuppliersRequest;
import com.rin.kanban.dto.response.SuppliersResponse;
import com.rin.kanban.service.SuppliersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SuppliersController {
    SuppliersService suppliersService;

    @PostMapping("create")
    public ApiResponse<SuppliersResponse> create(@RequestBody @Validated SuppliersRequest request) {
        return ApiResponse.<SuppliersResponse>builder()
                .result(suppliersService.create(request))
                .build();
    }

}
