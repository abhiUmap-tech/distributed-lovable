package com.projects.workspaceservice.services.impl;


import com.projects.commonlib.dto.PlanDto;
import com.projects.commonlib.error.BadRequestException;
import com.projects.commonlib.error.ResourceNotFoundException;
import com.projects.commonlib.security.AuthUtil;
import com.projects.workspaceservice.client.AccountClient;
import com.projects.workspaceservice.dto.project.ProjectRequest;
import com.projects.workspaceservice.dto.project.ProjectResponse;
import com.projects.workspaceservice.dto.project.ProjectSummaryResponse;
import com.projects.workspaceservice.entity.Project;
import com.projects.workspaceservice.entity.ProjectMember;
import com.projects.workspaceservice.entity.ProjectMemberId;
import com.projects.workspaceservice.enums.ProjectPermission;
import com.projects.workspaceservice.enums.ProjectRole;
import com.projects.workspaceservice.mapper.ProjectMapper;
import com.projects.workspaceservice.repository.ProjectMemberRepository;
import com.projects.workspaceservice.repository.ProjectRepository;
import com.projects.workspaceservice.security.SecurityExpressions;
import com.projects.workspaceservice.services.ProjectService;
import com.projects.workspaceservice.services.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    ProjectTemplateService projectTemplateService;
    AccountClient accountClient;
    SecurityExpressions securityExpressions;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        if(!canCreateProject()) {
            throw new BadRequestException("User cannot create a New project with current Plan, Upgrade plan now.");
        }

        Long ownerUserId = authUtil.getCurrentUserId();

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), ownerUserId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);

        projectTemplateService.initializeProjectFromTemplate(project.getId());

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projectWithRoles = projectRepository.findAllAccessibleByUser(userId);

        return projectWithRoles.stream()
                .map(projectWithRole -> projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        var projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(projectId, userId)
                .orElseThrow(() -> new BadRequestException("Project Not Found"));

        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    @Override
    public boolean hasPermission(Long projectId, ProjectPermission permission) {
        return securityExpressions.hasPermission(projectId, permission);
    }

    ///  INTERNAL FUNCTIONS

    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    private boolean canCreateProject() {
        Long userId = authUtil.getCurrentUserId();
        if (userId == null) {
            return false;
        }

        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();
        if (plan == null) {
            return false;  // No plan subscribed → cannot create project
        }

        int maxAllowed = plan.maxProjects();
        int ownedCount = projectMemberRepository.countOwnedProjectsByUserId(userId);

        return ownedCount < maxAllowed;
    }
}
