package com.projects.workspaceservice.services;


import com.projects.workspaceservice.dto.deploy.DeployResponse;
import jakarta.annotation.Nullable;

public interface DeploymentService {

    @Nullable DeployResponse deploy(Long projectId);
}
