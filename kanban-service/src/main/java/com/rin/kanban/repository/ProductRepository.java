package com.rin.kanban.repository;

import com.rin.kanban.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;


public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{'$or':  [{'isDeleted': null}, {'isDeleted':  false}]}")
    Page<Product> findAllProducts(Pageable pageable);
    Page<Product> findAllBySlugContaining(String slug, Pageable pageable);

    @Query("{'_id': {'$ne': ?0}, 'isDeleted': {'$in': [null, false]}, 'categoryIds': {'$in': ?1}}")
    List<Product> findByCategoryIdsInAndNotDeleted(String productId, Set<String> categoryIds, Pageable pageable);

}
