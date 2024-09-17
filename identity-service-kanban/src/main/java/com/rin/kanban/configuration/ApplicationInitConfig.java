package com.rin.kanban.configuration;

import com.rin.kanban.constant.PredefinedPermission;
import com.rin.kanban.constant.PredefinedRole;
import com.rin.kanban.entity.Permission;
import com.rin.kanban.entity.Role;
import com.rin.kanban.entity.User;
import com.rin.kanban.repository.PermissionRepository;
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

    private final String[] ADMIN_ROLE = new String[] { "ALL", "VIEW_ALL", "DELETE", "UPDATE"};
    private final String[] USER_ROLE = new String[] { "CREATE", "DELETE", "UPDATE", "VIEW_MY", "READ"};


    @Bean
    ApplicationRunner init(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {


            // Create admin user if it does not exist
            if (userRepository.findByEmail(ADMIN_USER_NAME).isEmpty()) {
                // Save permissions
                for (PredefinedPermission predefinedPermission : PredefinedPermission.values()) {
                    Permission permission = Permission.builder()
                            .name(predefinedPermission.name())
                            .description(predefinedPermission.getDescription())
                            .build();
                    permissionRepository.save(permission);
                }

                // Save roles
                Role adminRole = setPermissionToRole(PredefinedRole.ADMIN.name(), PredefinedRole.ADMIN.getDescription(), ADMIN_ROLE);
                roleRepository.save(adminRole);
                Role userRole = setPermissionToRole(PredefinedRole.USER.name(), PredefinedRole.USER.getDescription(), USER_ROLE);
                roleRepository.save(userRole);
                // Create admin

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
    private Role setPermissionToRole(String keyRole, String description, String...keyPermissions){
        var permission = new HashSet<Permission>();

        for (String keyPermission : keyPermissions) {
            Permission per = Permission.builder()
                    .name(keyPermission)
                    .build();
            permission.add(per);
        }

        return Role.builder()
                .name(keyRole)
                .description(description)
                .permissions(permission)
                .build();
    }

}
