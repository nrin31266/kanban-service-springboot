package com.rin.kanban.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierResponse {
    String id;
    String name;
    String photoUrl;
    String slug;
    String contactPerson;
    String email;
    Instant createdAt;
    Instant updatedAt;
}
