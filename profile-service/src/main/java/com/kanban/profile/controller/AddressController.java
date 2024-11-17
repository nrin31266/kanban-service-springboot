package com.kanban.profile.controller;

import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.request.AddressRequest;
import com.kanban.profile.dto.response.AddressResponse;
import com.kanban.profile.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AddressController {
    AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(addressRequest))
                .build();

    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> createAddress() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAddressesByUserId())
                .build();

    }

}
