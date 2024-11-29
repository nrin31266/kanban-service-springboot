package com.kanban.profile.mapper;

import com.kanban.profile.dto.response.ProvinceResponse;
import com.kanban.profile.dto.response.WardResponse;
import com.kanban.profile.entity.Province;
import com.kanban.profile.entity.Ward;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WardMapper {
    WardResponse toWardResponse(Ward ward);
}
