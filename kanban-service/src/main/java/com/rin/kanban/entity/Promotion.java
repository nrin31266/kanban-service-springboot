    package com.rin.kanban.entity;


    import com.rin.kanban.constant.DiscountType;
    import lombok.*;
    import lombok.experimental.FieldDefaults;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.Field;
    import org.springframework.data.mongodb.core.mapping.FieldType;
    import org.springframework.data.mongodb.core.mapping.MongoId;

    import java.math.BigDecimal;
    import java.time.Instant;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Document("promotion")
    @FieldDefaults( level = AccessLevel.PRIVATE)
    public class Promotion {
        @MongoId
        String id;
        String name;
        String description;
        String code;
        DiscountType discountType;
        @Field(targetType = FieldType.DECIMAL128)
        BigDecimal value;
        Instant start;
        Instant end;
        Integer quantity;
        @CreatedDate
        Instant createdAt;
        @LastModifiedDate
        Instant updatedAt;
        String imageUrl;
        @Builder.Default
        Boolean isDeleted=false;
    }
