package com.projects.workspaceservice.dto.member;

import com.projects.workspaceservice.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole role
) {
}
