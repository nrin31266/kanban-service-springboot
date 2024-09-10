package com.rin.kanban.configuration;

import com.rin.kanban.constant.PredefinedRole;
import com.rin.kanban.entity.Role;
import com.rin.kanban.entity.User;
import com.rin.kanban.repository.RoleRepository;
import com.rin.kanban.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner init(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            Role adminRole = roleRepository.save(
                    Role.builder()
                            .name(PredefinedRole.ADMIN.name())
                            .description("User role")
                            .permissions(PredefinedRole.ADMIN.getPermissions())
                            .build());
            roleRepository.save(
                    Role.builder()
                            .name(PredefinedRole.USER.name())
                            .description("User role")
                            .permissions(PredefinedRole.USER.getPermissions())
                            .build());
            if (userRepository.findByEmail(ADMIN_USER_NAME).isEmpty()) {

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User.builder()
                        .name("admin")
                        .email(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();
                userRepository.save(user);

                log.warn("Admin created: {}", user);
            }
        };
    }
}
