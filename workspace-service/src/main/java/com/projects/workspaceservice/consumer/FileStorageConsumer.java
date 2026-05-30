package com.projects.workspaceservice.consumer;

import com.projects.commonlib.event.FileStoreRequestEvent;
import com.projects.commonlib.event.FileStoreResponseEvent;
import com.projects.workspaceservice.entity.ProcessedEvent;
import com.projects.workspaceservice.repository.ProcessedEventRepository;
import com.projects.workspaceservice.services.ProjectFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageConsumer {

    private final ProjectFileService projectFileService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "file-storage-request-event", groupId = "workspace-group")
    @Transactional
    public void consumeFileEvent(FileStoreRequestEvent fileStoreRequestEvent){

        //Idempotency check
        if (processedEventRepository.existsById(fileStoreRequestEvent.sagaId())){
            log.info("Duplicate Saga detected: {}. Resending previous ACK", fileStoreRequestEvent.sagaId());
            sendResponse(fileStoreRequestEvent, true, null);
            return;
        }

        try {
            log.info("Saving file to MinIO: {}", fileStoreRequestEvent.filePath());

            projectFileService.saveFile(fileStoreRequestEvent.projectId(), fileStoreRequestEvent.filePath(),
                    fileStoreRequestEvent.content());

            processedEventRepository.save(new ProcessedEvent(
                    fileStoreRequestEvent.sagaId(),
                    LocalDateTime.now()));

            sendResponse(fileStoreRequestEvent, true, null);

        } catch (Exception e) {
            log.error("Error saving file: {}", e.getMessage());
            sendResponse(fileStoreRequestEvent, false, e.getMessage());
        }





    }

    private void sendResponse(FileStoreRequestEvent fileStoreRequestEvent, boolean success, String error) {
        var response = FileStoreResponseEvent.builder()
                .sagaId(fileStoreRequestEvent.sagaId())
                .projectId(fileStoreRequestEvent.projectId())
                .success(success)
                .errorMessage(error)
                .build();

        kafkaTemplate.send("file-store-response", response);

    }
}
