package com.rin.kanban.constant;

import com.rin.kanban.entity.Permission;
import com.rin.kanban.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PredefinedRole {

    ADMIN("Admin Role", "ADMIN", "READ_PRIVILEGE", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE"),
    USER("User Role", "USER", "READ_PRIVILEGE");

    private final String description;
    private final String roleName;
    private final Set<String> permissions;

    PredefinedRole(String description, String roleName, String... permissions) {
        this.description = description;
        this.roleName = roleName;
        this.permissions = Stream.of(permissions).collect(Collectors.toSet());
    }

    public String getDescription() {
        return description;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<Permission> getPermissions() {
        return permissions.stream()
                .map(permissionName -> new Permission(permissionName, permissionName + " Description"))
                .collect(Collectors.toSet());
    }
}
