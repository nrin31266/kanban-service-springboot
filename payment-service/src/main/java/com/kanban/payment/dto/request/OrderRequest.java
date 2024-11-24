package com.kanban.payment.dto.request;

import com.kanban.payment.constant.PaymentMethod;
import com.kanban.payment.constant.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String customerName;
    String customerEmail;
    String customerPhone;
    String customerAddress;
    Status status;
    PaymentMethod paymentMethod;
    String discountCode;
    List<ProductRequest> productRequests;
}
