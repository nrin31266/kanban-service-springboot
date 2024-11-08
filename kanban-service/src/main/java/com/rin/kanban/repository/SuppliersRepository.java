package com.rin.kanban.repository;

import com.rin.kanban.entity.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SuppliersRepository extends MongoRepository<Supplier, String> {

    Optional<Supplier> findByName(String name);
}
