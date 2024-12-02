package com.rin.kanban.repository;

import com.rin.kanban.constant.OtpType;
import com.rin.kanban.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, String> {
    Optional<Otp> findFirstByUser_IdOrderByCreatedAtDesc(String id);
}
