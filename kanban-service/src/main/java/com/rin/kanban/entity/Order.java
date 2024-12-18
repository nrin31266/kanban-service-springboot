package com.rin.kanban.entity;


import com.rin.kanban.constant.PaymentMethod;
import com.rin.kanban.constant.Status;
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

@Document("orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @MongoId
    String id;
    String userId;
    String customerName;
    String customerPhone;
    String customerEmail;
    String customerAddress;
    PaymentMethod paymentMethod;
    String discountCode;
    BigDecimal reduction;
    Boolean isComplete;
    Status status;
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal amount;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
}
