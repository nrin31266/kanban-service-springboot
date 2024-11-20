package com.kanban.profile.repository;


import com.kanban.profile.entity.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface ProfileRepository extends MongoRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);
}
