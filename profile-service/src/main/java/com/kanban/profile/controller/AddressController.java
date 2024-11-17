package com.kanban.profile.controller;

import com.kanban.event.dto.UpdateAddressIsDefaultEvent;
import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.request.AddressRequest;
import com.kanban.profile.dto.response.AddressResponse;
import com.kanban.profile.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Slf4j
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

    @KafkaListener(topics = "update-address-is-default")
    public void listenNotificationVerifyOtpCodeWithEmail(UpdateAddressIsDefaultEvent request) {
        log.info("Message received: {}", request);
        addressService.updateAddressIsDefault(request);
    }

}
