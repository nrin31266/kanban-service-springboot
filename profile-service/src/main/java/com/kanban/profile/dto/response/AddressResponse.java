package com.kanban.profile.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    String id;
    String userId;
    String address;
    String name;
    String phoneNumber;
    Boolean isDefault;
    Instant createdAt;
    Instant updatedAt;
}
