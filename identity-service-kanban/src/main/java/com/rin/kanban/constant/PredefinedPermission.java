package com.rin.kanban.constant;

import lombok.Getter;

@Getter
public enum PredefinedPermission {
    ALL("Permission ALL"),
    CREATE("Permission"),
    DELETE("Permission"),
    UPDATE("Permission"),
    VIEW_ALL("Permission"),
    VIEW_MY("Permission"),
    READ("Permission"),
    SHARE("Permission");

    private final String description;

    PredefinedPermission(String description) {
        this.description = description;
    }

}
