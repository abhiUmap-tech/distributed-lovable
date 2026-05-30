package com.projects.workspaceservice.services.impl;


import com.projects.commonlib.error.ResourceNotFoundException;
import com.projects.commonlib.security.AuthUtil;
import com.projects.workspaceservice.client.AccountClient;
import com.projects.workspaceservice.dto.member.InviteMemberRequest;
import com.projects.workspaceservice.dto.member.MemberResponse;
import com.projects.workspaceservice.dto.member.UpdateMemberRoleRequest;
import com.projects.workspaceservice.entity.Project;
import com.projects.workspaceservice.entity.ProjectMember;
import com.projects.workspaceservice.entity.ProjectMemberId;
import com.projects.workspaceservice.mapper.ProjectMemberMapper;
import com.projects.workspaceservice.repository.ProjectMemberRepository;
import com.projects.workspaceservice.repository.ProjectRepository;
import com.projects.workspaceservice.services.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    AuthUtil authUtil;
    AccountClient accountClient;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        var userId = authUtil.getCurrentUserId();
        var project = getAccessibleProjectById(projectId, userId);

        var invitee = accountClient.getUserByEmail(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        //Owner cannot invite himself again
        if (invitee.id().equals(userId))
            throw new RuntimeException("Cannot invite yourself");


       var projectMemberId = new ProjectMemberId(projectId, invitee.id());

        //Member is already invited to the project
       if (projectMemberRepository.existsById(projectMemberId))
           throw new RuntimeException("Cannot invite once again");

       var projectMember = ProjectMember.builder()
               .id(projectMemberId)
               .project(project)
               .projectRole(request.role())
               .invitedAt(Instant.now())
               .build();

       projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, UpdateMemberRoleRequest request, Long memberId) {
        var userId = authUtil.getCurrentUserId();
        var project = getAccessibleProjectById(projectId, userId);

        var projectMemberId = new ProjectMemberId(projectId, memberId);
        var projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long memberId, Long projectId) {
        var userId = authUtil.getCurrentUserId();
        var project = getAccessibleProjectById(projectId, userId);

        var projectMemberId = new ProjectMemberId(projectId, memberId);

        //Check if member already exist before removing
        if (!projectMemberRepository.existsById(projectMemberId))
            throw new RuntimeException("Member does not exist");

        projectMemberRepository.deleteById(projectMemberId);


    }

    public Project getAccessibleProjectById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
