package com.projects.workspaceservice.services;


import com.projects.workspaceservice.dto.project.ProjectRequest;
import com.projects.workspaceservice.dto.project.ProjectResponse;
import com.projects.workspaceservice.dto.project.ProjectSummaryResponse;
import com.projects.workspaceservice.enums.ProjectPermission;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);

    boolean hasPermission(Long projectId, ProjectPermission permission);
}
