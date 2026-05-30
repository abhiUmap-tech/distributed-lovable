package com.projects.intelligenceservice.services.impl;


import com.projects.commonlib.enums.ChatEventStatus;
import com.projects.commonlib.event.FileStoreRequestEvent;
import com.projects.commonlib.security.AuthUtil;
import com.projects.intelligenceservice.client.WorkspaceClient;
import com.projects.intelligenceservice.dto.chat.StreamResponse;
import com.projects.intelligenceservice.entity.ChatEvent;
import com.projects.intelligenceservice.entity.ChatMessage;
import com.projects.intelligenceservice.entity.ChatSession;
import com.projects.intelligenceservice.entity.ChatSessionId;
import com.projects.intelligenceservice.enums.ChatEventType;
import com.projects.intelligenceservice.enums.MessageRole;
import com.projects.intelligenceservice.llm.PromptUtils;
import com.projects.intelligenceservice.llm.advisors.FileTreeContextAdvisor;
import com.projects.intelligenceservice.llm.parser.LlmResponseParser;
import com.projects.intelligenceservice.llm.tools.CodeGenerationTools;
import com.projects.intelligenceservice.repository.ChatEventRepository;
import com.projects.intelligenceservice.repository.ChatMessageRepository;
import com.projects.intelligenceservice.repository.ChatSessionRepository;
import com.projects.intelligenceservice.services.AIGenerationService;
import com.projects.intelligenceservice.services.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGenerationServiceImpl implements AIGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final ChatSessionRepository chatSessionRepository;
    private final LlmResponseParser llmResponseParser;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;
    private final WorkspaceClient workspaceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {

//        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectId, workspaceClient);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                            advisorSpec.params(advisorParams);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    if (response.getResults() != null && !response.getResults().isEmpty()) {
                        String content = response.getResult().getOutput().getText();

                        if(content != null && !content.isEmpty() && endTime.get() == 0) { // first non-empty chunk received
                            endTime.set(System.currentTimeMillis());
                        }
                        if(response.getMetadata().getUsage() != null) {
                            usageRef.set(response.getMetadata().getUsage());
                        }
                        fullResponseBuffer.append(content);
                    }

                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);

                        long duration = (endTime.get() - startTime.get()) /  1000;
                        finalizeChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get(), userId);
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .map(response -> {
                    if (response.getResults() != null && !response.getResults().isEmpty()) {
                        String text = response.getResult().getOutput().getText();
                        return new StreamResponse(text != null ? text : "");
                    }
                    return new StreamResponse("");
                });
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage, Long userId) {
        Long projectId = chatSession.getId().getProjectId();

        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getId().getUserId(), totalTokens);
        }

        // Save the User message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .messageRole(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .messageRole(MessageRole.ASSISTANT)
                .content("Assistant Message here...")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                .type(ChatEventType.THOUGHT)
                .status(ChatEventStatus.CONFIRMED)
                .chatMessage(assistantChatMessage)
                .content("Thought for "+duration+"s")
                .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e ->{

                         var sagaId= UUID.randomUUID().toString();
                         e.setSagaId(sagaId);

                         var fileStoreRequestEvent = new FileStoreRequestEvent(projectId, sagaId,
                                 e.getFilePath(), e.getContent(),
                                 userId);
                         log.info("Storage request event send: {}", e.getFilePath());
                         kafkaTemplate.send("file-storage-request-event", "project-" + projectId, fileStoreRequestEvent);
                }) ;

        chatEventRepository.saveAll(chatEventList);
    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession == null) {


            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }

}