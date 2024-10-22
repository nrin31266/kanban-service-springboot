package com.rin.kanban.repository;

import com.rin.kanban.entity.SubProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SubProductRepository extends MongoRepository<SubProduct, String> {
    List<SubProduct> findByProductId(String productId);
    List<SubProduct> findAllByProductId(String productId);
    void deleteAllByProductId(String productId);
    @Query(value = "{}", fields = "{'color' : 1, 'size' : 1 }")
    List<SubProduct> findIdAndName();
}
