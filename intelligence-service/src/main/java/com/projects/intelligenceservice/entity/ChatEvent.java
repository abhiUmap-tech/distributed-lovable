package com.projects.intelligenceservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.commonlib.enums.ChatEventStatus;
import com.projects.intelligenceservice.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "chat_events")
public class ChatEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnore
    ChatMessage chatMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChatEventType type;

    @Column(nullable = false)
    Integer sequenceOrder;

    @Column(columnDefinition = "text")
    String content;

    String filePath;

    @Column(columnDefinition = "text")
    String metadata;

    String sagaId;

    @Enumerated(EnumType.STRING)
    ChatEventStatus status;
}


