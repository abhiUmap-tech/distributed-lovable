package com.projects.workspaceservice.controller;


import com.projects.workspaceservice.dto.member.InviteMemberRequest;
import com.projects.workspaceservice.dto.member.MemberResponse;
import com.projects.workspaceservice.dto.member.UpdateMemberRoleRequest;
import com.projects.workspaceservice.services.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId){
        var userId = 1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> invitedMember(
            @PathVariable Long projectId,
            @Valid @RequestBody InviteMemberRequest request){
        var userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMember(projectId, request));

    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateMemberRoleRequest request){

        var userId = 1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, request, memberId));

    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId){

        var userId = 1L;
        projectMemberService.removeProjectMember(memberId, projectId);
        return ResponseEntity.noContent().build();

    }


}
