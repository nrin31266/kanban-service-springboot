package com.rin.kanban.repository;

import com.rin.kanban.entity.BillItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BillItemRepository extends MongoRepository<BillItem, String> {
}
