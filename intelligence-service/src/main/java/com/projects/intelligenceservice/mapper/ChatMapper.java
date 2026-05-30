package com.projects.intelligenceservice.mapper;

import com.projects.intelligenceservice.dto.chat.ChatResponse;
import com.projects.intelligenceservice.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
