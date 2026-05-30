package com.projects.workspaceservice.mapper;


import com.projects.workspaceservice.dto.project.ProjectResponse;
import com.projects.workspaceservice.dto.project.ProjectSummaryResponse;
import com.projects.workspaceservice.entity.Project;
import com.projects.workspaceservice.enums.ProjectRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole projectRole);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects);

}
