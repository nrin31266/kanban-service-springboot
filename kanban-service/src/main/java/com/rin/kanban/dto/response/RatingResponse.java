package com.rin.kanban.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingResponse {
    String id;
    String subProductId;
    String productId;
    String orderId;
    String orderProductId;
    int rating;
    String comment;
    List<String> imageUrls;
    String userId;
    Instant createdAt;
    Instant updatedAt;
    String created;
    String updated;
    //
    String avatar;
    String name;

}
