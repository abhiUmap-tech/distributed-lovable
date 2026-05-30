package com.projects.workspaceservice.security;

import com.projects.commonlib.security.AuthUtil;
import com.projects.workspaceservice.enums.ProjectPermission;
import com.projects.workspaceservice.repository.ProjectMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityExpressions {

    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    public boolean hasPermission(Long projectId, ProjectPermission projectPermission){
        var userId = authUtil.getCurrentUserId();

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
    }

    public boolean canViewProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.VIEW);

    }

    public boolean canEditProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.EDIT);

    }

    public boolean canDeleteProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.DELETE);

    }

    public boolean canViewMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);

    }

    public boolean canManageMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);

    }

}
