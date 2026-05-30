package com.projects.intelligenceservice.repository;


import com.projects.intelligenceservice.entity.ChatSession;
import com.projects.intelligenceservice.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
