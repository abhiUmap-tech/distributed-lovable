package com.projects.intelligenceservice.client;

import com.projects.commonlib.dto.FileTreeDto;
import com.projects.intelligenceservice.enums.ProjectPermission;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "workspace-service", path = "/workspace", url = "${WORKSPACE_SERVICE_URI:}")
public interface WorkspaceClient {

    @GetMapping("/internal/v1/projects/{projectId}/files/tree")
    FileTreeDto getFileTree(@PathVariable("projectId") Long projectId);

    @GetMapping("/internal/v1/projects/{projectId}/files/content")
    String getFileContent(@PathVariable("projectId") Long projectId, @RequestParam("path") String path);

    @GetMapping("/internal/v1/projects/{projectId}/permissions/check")
    boolean checkPermission(
            @PathVariable("projectId") Long projectId,
            @RequestParam("permission") ProjectPermission permission);


}
