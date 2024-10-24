package com.rin.kanban.repository;

import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface SubProductRepository extends MongoRepository<SubProduct, String> {
    List<SubProduct> findByProductId(String productId);

    List<SubProduct> findAllByProductId(String productId);

    void deleteAllByProductId(String productId);

    @Query(value = "{}", fields = "{'color' : 1, 'size' : 1 }")
    List<SubProduct> findColorsAndSizeInSubProducts();

    @Query("{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ], 'productId': ?0 }")
    List<SubProduct> findAllByProductIdAndIsDeletedNullOrFalse(String productId, Pageable pageable);

    @Query("{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ] }")
    List<SubProduct> findAllActiveSubProducts(Pageable pageable);

    @Query( "{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ], 'productId': ?0 }")
    List<SubProduct> findAllByProductIdAndIsDeletedNullOrFalse(String productId);

    @Query("{ '$or': [ { 'isDeleted': false }, { 'isDeleted': null } ] }")
    List<SubProduct> findAllActiveSubProducts();


}
