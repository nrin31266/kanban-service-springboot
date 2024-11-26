package com.rin.kanban.repository;


import com.rin.kanban.constant.Status;
import com.rin.kanban.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface OrderRepository extends MongoRepository<com.rin.kanban.entity.Order, String> {
    Page<Order> findByStatus(Status status, Pageable pageable);
    List<Order> findByUserIdAndStatusOrderByUpdatedAtDesc(String userId, Status status);
}
