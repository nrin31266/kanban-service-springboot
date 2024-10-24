package com.rin.kanban.repository;

import com.rin.kanban.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{'$or':  [{'isDeleted': null}, {'isDeleted':  false}]}")
    Page<Product> findAllByIsDeletedIsNullOrFalse(Pageable pageable);
    Page<Product> findAllBySlugContaining(String slug, Pageable pageable);
}
