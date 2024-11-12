package com.rin.kanban.repository;

import com.rin.kanban.dto.request.CartRequest;
import com.rin.kanban.entity.Cart;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    @Query("{$and:  [{subProductId:  ?0}, {createdBy:  ?1}]}")
    Optional<Cart> findCart(String subProductId, String createdBy);

    List<Cart> findAllByCreatedByOrderByUpdatedAtDesc(String createdBy);

}
