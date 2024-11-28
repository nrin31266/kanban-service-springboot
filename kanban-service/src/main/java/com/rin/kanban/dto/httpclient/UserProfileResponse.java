package com.rin.kanban.dto.httpclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String userId;
    String name;
    String phone;
    LocalDate dob;
    String avatar;
    int gender;
    Instant createdAt;
    Instant updatedAt;
}
