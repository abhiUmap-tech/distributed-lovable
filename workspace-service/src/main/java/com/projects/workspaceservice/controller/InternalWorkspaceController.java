package com.projects.workspaceservice.controller;

import com.projects.commonlib.dto.FileTreeDto;
import com.projects.workspaceservice.enums.ProjectPermission;
import com.projects.workspaceservice.services.ProjectFileService;
import com.projects.workspaceservice.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1")
public class InternalWorkspaceController {

    private final ProjectService projectService;
    private final ProjectFileService projectFileService;

    @GetMapping("/projects/{projectId}/files/tree")
    public FileTreeDto getFileTree(@PathVariable Long projectId){
        return projectFileService.getFileTree(projectId);
    }

    @GetMapping("/projects/{projectId}/files/content")
    public String getFileContent(@PathVariable Long projectId, @RequestParam String path){
        return projectFileService.getFileContent(projectId, path);
    }

    @GetMapping("/projects/{projectId}/permissions/check")
    public boolean checkProjectPermission(
            @PathVariable Long projectId,
            @RequestParam ProjectPermission permission) {
        return projectService.hasPermission(projectId, permission);
    }





}
