package com.projects.workspaceservice.services;

import com.projects.workspaceservice.dto.member.InviteMemberRequest;
import com.projects.workspaceservice.dto.member.MemberResponse;
import com.projects.workspaceservice.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, UpdateMemberRoleRequest request, Long memberId);

    void removeProjectMember(Long memberId, Long projectId);
}
