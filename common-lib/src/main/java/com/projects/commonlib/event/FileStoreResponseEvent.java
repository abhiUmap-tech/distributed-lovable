package com.projects.commonlib.event;

import lombok.Builder;

@Builder
public record FileStoreResponseEvent(
        String sagaId,
        boolean success,
        String errorMessage,
        Long projectId
) {
}
