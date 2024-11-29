package com.rin.kanban.repository.custom;

import java.math.BigDecimal;

public interface OrderCustomRepository {
    BigDecimal getSoldCountByProductId(String productId);
}
