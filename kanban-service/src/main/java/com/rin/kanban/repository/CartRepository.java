package com.rin.kanban.repository;

import com.rin.kanban.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    @Query("{ 'subProductId' : ?0, 'createdBy' : ?1 }")
    Optional<Cart> findCart(String subProductId, String createdBy);

    Optional<Cart> findCartBySubProductId(String subProductId);

    Optional<Cart> findFirstByCreatedBy(String createdBy);

    Page<Cart> findAllByCreatedByOrderByUpdatedAtDesc(String createdBy, Pageable pageable);

}
