package com.kanban.profile.controller;

import com.kanban.profile.dto.ApiResponse;
import com.kanban.profile.dto.response.DistrictResponse;
import com.kanban.profile.dto.response.ProvinceResponse;
import com.kanban.profile.dto.response.WardResponse;
import com.kanban.profile.service.LocationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/provinces/getAll")
    public ApiResponse<List<ProvinceResponse>> getAllProvinces(
            @RequestParam(defaultValue = "-1") int limit) {
        List<ProvinceResponse> provinces = locationService.getAllProvinces(limit);
        return ApiResponse.<List<ProvinceResponse>>builder()
                .result(provinces)
                .build();
    }

    @GetMapping("/districts/getByProvince")
    public ApiResponse<List<DistrictResponse>> getDistrictsByProvince(
            @RequestParam String provinceCode,
            @RequestParam(defaultValue = "-1") int limit) {
        List<DistrictResponse> districts = locationService.getDistrictsByProvince(provinceCode, limit);
        return ApiResponse.<List<DistrictResponse>>builder()
                .result(districts)
                .build();
    }

    @GetMapping("/wards/getByDistrict")
    public ApiResponse<List<WardResponse>> getWardsByDistrict(
            @RequestParam String districtCode,
            @RequestParam(defaultValue = "-1") int limit) {
        List<WardResponse> wards = locationService.getWardsByDistrict(districtCode, limit);
        return ApiResponse.<List<WardResponse>>builder()
                .result(wards)
                .build();
    }
}
