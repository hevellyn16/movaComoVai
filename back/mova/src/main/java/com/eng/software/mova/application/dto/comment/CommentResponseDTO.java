package com.eng.software.mova.application.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Dados retornados ao consultar ou manipular um comentário")
public record CommentResponseDTO(
        @Schema(
            description = "Identificador único do comentário",
            example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
        )
        UUID id,

        @Schema(
            description = "Texto do comentário",
            example = "Que evento incrível! Mal posso esperar."
        )
        String content,

        @Schema(
            description = "Identificador único do usuário autor do comentário",
            example = "a1b2c3d4-e5f6-7890-abcd-111122223333"
        )
        UUID userId,

        @Schema(
            description = "Identificador único do evento ao qual este comentário pertence",
            example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
        )
        UUID eventId,

        @Schema(
            description = "Data e hora em que o comentário foi criado",
            example = "2025-06-01T15:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
            description = "Data e hora da última atualização do comentário",
            example = "2025-06-01T16:00:00"
        )
        LocalDateTime updatedAt
) {
}
