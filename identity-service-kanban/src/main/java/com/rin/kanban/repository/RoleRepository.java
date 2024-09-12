package com.rin.kanban.repository;

import com.rin.kanban.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, String> {
}
