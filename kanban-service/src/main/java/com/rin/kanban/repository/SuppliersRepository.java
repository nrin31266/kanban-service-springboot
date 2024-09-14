package com.rin.kanban.repository;

import com.rin.kanban.entity.Suppliers;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SuppliersRepository extends MongoRepository<Suppliers, String> {
    Optional<Suppliers> findByContact(String contact);
}
