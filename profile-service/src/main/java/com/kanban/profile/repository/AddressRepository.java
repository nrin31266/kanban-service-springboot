package com.kanban.profile.repository;

import com.kanban.profile.entity.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findByUserIdOrderByUpdatedAtDesc(String userId);
}
