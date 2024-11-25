package com.rin.kanban.dto.request;


import com.rin.kanban.constant.PaymentMethod;
import com.rin.kanban.constant.Status;
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
    List<OrderProductRequest> orderProductRequests;
}
