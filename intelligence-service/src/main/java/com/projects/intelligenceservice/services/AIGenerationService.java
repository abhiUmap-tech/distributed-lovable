package com.projects.intelligenceservice.services;


import com.projects.intelligenceservice.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AIGenerationService {
    Flux<StreamResponse> streamResponse(String message, Long projectId);
}
