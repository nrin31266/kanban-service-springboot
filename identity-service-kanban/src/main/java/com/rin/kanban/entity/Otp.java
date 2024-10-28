package com.rin.kanban.entity;

import com.rin.kanban.constant.OtpType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String type; // EMAIL, SMS,...
    String otp;
    boolean verified;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @CreatedDate
    Instant createdAt;
    Instant expiresAt;
}
