package com.rin.kanban.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<com.rin.kanban.entity.Order, String> {
}
