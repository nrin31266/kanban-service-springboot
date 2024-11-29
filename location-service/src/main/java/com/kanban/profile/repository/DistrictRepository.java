package com.kanban.profile.repository;

import com.kanban.profile.entity.District;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DistrictRepository extends MongoRepository<District, String> {
    List<District> findByParentCode(String parentCode);
}
