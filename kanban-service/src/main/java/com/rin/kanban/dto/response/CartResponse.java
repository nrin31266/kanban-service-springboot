package com.rin.kanban.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    String createdBy;
    String subProductId;
    int count;
    int quantity;
    Map<String, String> options;
    Instant createdAt;
    Instant updatedAt;
}
