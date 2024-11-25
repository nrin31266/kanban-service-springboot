package com.rin.kanban.repository;


import com.rin.kanban.entity.OrderProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProductsRepository extends MongoRepository<OrderProduct, String> {
}
