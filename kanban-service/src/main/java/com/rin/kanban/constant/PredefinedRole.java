
package com.rin.kanban.constant;

import lombok.Getter;

import java.util.Set;

@Getter
public enum PredefinedRole {
    ADMIN(PermissionOfRole.ADMIN_PERMISSIONS),
    USER(PermissionOfRole.USER_PERMISSIONS);

    private final Set<String> permissions;

    PredefinedRole(Set<String> permissions) {
        this.permissions = permissions;
    }
}
