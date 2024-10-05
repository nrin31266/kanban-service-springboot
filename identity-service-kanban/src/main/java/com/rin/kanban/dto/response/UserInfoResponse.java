package com.rin.kanban.dto.response;

import com.rin.kanban.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoResponse {
    String id;
    String name;
    String email;
    Set<Role> roles;
}
