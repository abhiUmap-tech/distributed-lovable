package com.projects.intelligenceservice.services;


import com.projects.intelligenceservice.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}
