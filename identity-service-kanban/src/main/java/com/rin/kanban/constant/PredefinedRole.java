package com.rin.kanban.constant;

import lombok.Getter;

@Getter
public enum PredefinedRole {
    ADMIN("Admin role"),
    USER("User role");

    private final String description;

    PredefinedRole(String description) {
        this.description = description;
    }
}
