package com.kanban.profile.mapper;

import com.kanban.profile.dto.response.DistrictResponse;
import com.kanban.profile.dto.response.ProvinceResponse;
import com.kanban.profile.entity.District;
import com.kanban.profile.entity.Province;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DistrictMapper {
    DistrictResponse toDistrictResponse(District district);
}
