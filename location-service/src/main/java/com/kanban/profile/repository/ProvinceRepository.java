package com.kanban.profile.repository;

import com.kanban.profile.entity.Province;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProvinceRepository extends MongoRepository<Province, String> {
}
