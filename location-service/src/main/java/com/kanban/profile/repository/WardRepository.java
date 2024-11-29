package com.kanban.profile.repository;

import com.kanban.profile.entity.Ward;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WardRepository extends MongoRepository<Ward, String> {
    List<Ward> findByParentCode(String parentCode);
}
