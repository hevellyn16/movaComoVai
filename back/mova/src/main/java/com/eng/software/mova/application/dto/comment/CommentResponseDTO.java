package com.eng.software.mova.application.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentResponseDTO(
        UUID id,
        String content,
        UUID userId,
        String userName,
        String userAvatarUrl,
        UUID eventId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
