package com.projects.intelligenceservice.services.impl;

import com.projects.commonlib.security.AuthUtil;
import com.projects.intelligenceservice.dto.chat.ChatResponse;
import com.projects.intelligenceservice.entity.ChatSessionId;
import com.projects.intelligenceservice.mapper.ChatMapper;
import com.projects.intelligenceservice.repository.ChatMessageRepository;
import com.projects.intelligenceservice.repository.ChatSessionRepository;
import com.projects.intelligenceservice.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        var userId = authUtil.getCurrentUserId();

        var chatSession = chatSessionRepository.getReferenceById(new ChatSessionId(projectId, userId));

        var chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
