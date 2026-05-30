package com.projects.workspaceservice.enums;

import lombok.Getter;

import java.util.Set;

import static com.projects.workspaceservice.enums.ProjectPermission.*;

@Getter
public enum ProjectRole {
    // 1. Pass the permissions to the constructor
    EDITOR(EDIT, DELETE, VIEW_MEMBERS),
    VIEWER(VIEW, VIEW_MEMBERS),
    OWNER(VIEW, EDIT, DELETE, VIEW_MEMBERS, MANAGE_MEMBERS);

    // 2. You MUST declare the field here
    private final Set<ProjectPermission> permissions;

    // 3. Manual constructor to handle the varargs (...)
    ProjectRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }




}
