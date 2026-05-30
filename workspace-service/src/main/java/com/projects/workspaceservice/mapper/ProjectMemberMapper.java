package com.projects.workspaceservice.mapper;


import com.projects.workspaceservice.dto.member.MemberResponse;
import com.projects.workspaceservice.entity.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "id.userId")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);
}
