package com.rin.kanban.repository;

import com.rin.kanban.entity.InvalidatedToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends MongoRepository<InvalidatedToken, String> {}
