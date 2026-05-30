package com.projects.workspaceservice.dto.project;



import com.projects.workspaceservice.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole role) { }
