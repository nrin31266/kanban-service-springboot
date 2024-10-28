package com.rin.kanban.dto.response;

import com.rin.kanban.constant.OtpType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpResponse {
    String otp;
    String type;
}
