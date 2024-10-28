package com.rin.kanban.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    @Column(unique = true)
    String email;
    String password;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
    @ManyToMany
    Set<Role> roles;
    @OneToMany(mappedBy = "user")
    Set<Otp> otp;
}
