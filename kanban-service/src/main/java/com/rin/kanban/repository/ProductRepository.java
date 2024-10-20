package com.rin.kanban.repository;

import com.rin.kanban.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<Product, String> {
}
