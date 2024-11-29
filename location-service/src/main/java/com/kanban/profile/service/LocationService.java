package com.kanban.profile.service;

import com.kanban.profile.dto.response.DistrictResponse;
import com.kanban.profile.dto.response.ProvinceResponse;
import com.kanban.profile.dto.response.WardResponse;
import com.kanban.profile.entity.District;
import com.kanban.profile.entity.Province;
import com.kanban.profile.entity.Ward;
import com.kanban.profile.mapper.DistrictMapper;
import com.kanban.profile.mapper.ProvinceMapper;
import com.kanban.profile.mapper.WardMapper;
import com.kanban.profile.repository.DistrictRepository;
import com.kanban.profile.repository.ProvinceRepository;
import com.kanban.profile.repository.WardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LocationService {
    ProvinceRepository provinceRepository;
    DistrictRepository districtRepository;
    WardRepository wardRepository;
    ProvinceMapper provinceMapper;
    DistrictMapper districtMapper;
    WardMapper wardMapper;

    public List<ProvinceResponse> getAllProvinces(int limit) {
        List<Province> provinces = provinceRepository.findAll();

        log.info(provinces.toString());
        if (limit >= 0) {
            provinces = provinces.stream().limit(limit).toList();
        }

        return provinces.stream()
                .map(provinceMapper::toProvinceResponse)
                .collect(Collectors.toList());
    }

    public List<DistrictResponse> getDistrictsByProvince(String provinceCode, int limit) {
        List<District> districts = districtRepository.findByParentCode(provinceCode);

        if (limit >= 0) {
            districts = districts.stream().limit(limit).toList();
        }

        return districts.stream()
                .map(districtMapper::toDistrictResponse)
                .collect(Collectors.toList());
    }

    public List<WardResponse> getWardsByDistrict(String districtCode, int limit) {
        List<Ward> wards = wardRepository.findByParentCode(districtCode);

        if (limit >= 0) {
            wards = wards.stream().limit(limit).toList();
        }

        return wards.stream()
                .map(wardMapper::toWardResponse)
                .collect(Collectors.toList());
    }

}
