package com.projects.workspaceservice.dto.member;


import com.projects.workspaceservice.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt

) {
}
