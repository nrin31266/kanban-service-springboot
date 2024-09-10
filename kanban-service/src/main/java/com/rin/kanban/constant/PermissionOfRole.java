package com.rin.kanban.constant;

import java.util.Set;
import java.util.HashSet;

public class PermissionOfRole {
    public static final Set<String> ADMIN_PERMISSIONS = new HashSet<>(Set.of(
            "CREATE_TASK",
            "UPDATE_TASK",
            "DELETE_TASK",
            "VIEW_ALL_TASKS"
    ));

    public static final Set<String> USER_PERMISSIONS = new HashSet<>(Set.of(
            "CREATE_TASK",
            "VIEW_OWN_TASKS",
            "DELETE_MY_TASK"
    ));
}
