package com.kanban.payment.dto.response;

import com.kanban.payment.constant.PaymentMethod;
import com.kanban.payment.constant.Status;
import com.kanban.payment.entity.Product;
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
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userId;
    String customerName;
    String customerPhone;
    String customerEmail;
    String customerAddress;
    PaymentMethod paymentMethod;
    String discountCode;
    Status status;
    BigDecimal amount;
    Instant createdAt;
    Instant updatedAt;

    List<Product> products;
}
