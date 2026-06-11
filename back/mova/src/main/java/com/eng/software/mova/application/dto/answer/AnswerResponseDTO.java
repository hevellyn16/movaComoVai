package com.eng.software.mova.application.dto.answer;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AnswerResponseDTO(
        UUID id,
        String answer,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID userId,
        UUID commentId
) {
}
