package com.projects.workspaceservice.dto.project;

public record FileContentResponse(
        String path,
        String content
) {
}
