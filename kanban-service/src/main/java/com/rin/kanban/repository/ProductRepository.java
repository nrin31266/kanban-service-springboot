package com.rin.kanban.repository;

import com.rin.kanban.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findAllByIsDeletedIsNullOrIsDeletedIsFalse();
    Page<Product> findAllByIsDeletedIsNullOrIsDeletedIsFalse(Pageable pageable);
    Page<Product> findAllBySlugContaining(String slug, Pageable pageable);
}
