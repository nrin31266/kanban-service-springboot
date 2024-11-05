package com.rin.kanban.repository;

import com.rin.kanban.entity.SubProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubProductRepository extends MongoRepository<SubProduct, String> {

    void deleteAllByProductId(String productId);

    @Query(value = "{}", fields = "{'color' : 1, 'size' : 1 }")
    List<SubProduct> findColorsAndSizeInSubProducts();

    @Query( "{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ], 'productId': ?0 }")
    List<SubProduct> findAllByProductIdAndIsDeletedNullOrFalse(String productId);

    @Query( "{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ]}")
    Page<SubProduct> findSubProducts(Pageable pageable);
}
