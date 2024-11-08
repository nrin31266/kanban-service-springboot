package com.rin.kanban.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierRequest {
    String name;
    String photoUrl;
    String slug;
    String contactPerson;
    String email;
}
