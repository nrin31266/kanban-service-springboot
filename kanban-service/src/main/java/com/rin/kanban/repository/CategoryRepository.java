package com.rin.kanban.repository;

import com.rin.kanban.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByParentIdAndSlug(String parentId, String slug);
    List<Category> findAllByParentId(String parentId);
    @Query("{$or : [{isDeleted:  false}, {isDeleted: null}] , parentId: null}")
    List<Category> findAllRootCategories();
}
