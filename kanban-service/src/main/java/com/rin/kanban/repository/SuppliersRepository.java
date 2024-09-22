package com.rin.kanban.repository;

import com.rin.kanban.entity.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface SuppliersRepository extends MongoRepository<Supplier, String> {

}
