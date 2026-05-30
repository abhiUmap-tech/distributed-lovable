package com.projects.workspaceservice.mapper;

import com.projects.commonlib.dto.FileNode;
import com.projects.workspaceservice.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
