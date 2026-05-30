package com.projects.workspaceservice.services.impl;


import com.projects.commonlib.error.ResourceNotFoundException;
import com.projects.workspaceservice.entity.ProjectFile;
import com.projects.workspaceservice.repository.ProjectFileRepository;
import com.projects.workspaceservice.repository.ProjectRepository;
import com.projects.workspaceservice.services.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET = "starter-project";
    private static final String TARGET_BUCKET = "projects";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter-main";


    @Override
    public void initializeProjectFromTemplate(Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db

            log.info("Copying template files for projectId={}", projectId);


            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(CopySource
                                        .builder()
                                        .bucket(TEMPLATE_BUCKET)
                                        .object(sourceKey)
                                        .build())
                                .build());

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
            }

            log.info("Saving {} project files metadata", filesToSave.size());
            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }

    }

}

