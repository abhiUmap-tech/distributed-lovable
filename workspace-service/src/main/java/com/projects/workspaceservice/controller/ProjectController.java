package com.projects.workspaceservice.controller;

import com.projects.workspaceservice.dto.deploy.DeployResponse;
import com.projects.workspaceservice.dto.project.ProjectRequest;
import com.projects.workspaceservice.dto.project.ProjectResponse;
import com.projects.workspaceservice.dto.project.ProjectSummaryResponse;
import com.projects.workspaceservice.services.DeploymentService;
import com.projects.workspaceservice.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@Slf4j
@RequiredArgsConstructor
public class ProjectController {


    private final ProjectService projectService;
    private final DeploymentService deploymentService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectSummaryResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getUserProjectById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        log.info("Create project API called with name: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deploy")
    public ResponseEntity<DeployResponse> deployProject(@PathVariable Long id){
        return ResponseEntity.ok(deploymentService.deploy(id));

    }


}
