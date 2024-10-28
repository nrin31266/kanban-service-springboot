package com.rin.kanban.mapper;

import com.rin.kanban.dto.response.OtpResponse;
import com.rin.kanban.entity.Otp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OtpMapper {
    OtpResponse toOtpResponse(Otp otp);
}
