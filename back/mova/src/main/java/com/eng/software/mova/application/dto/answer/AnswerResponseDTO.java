package com.eng.software.mova.application.dto.answer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Dados retornados ao consultar ou manipular uma resposta")
public record AnswerResponseDTO(
        @Schema(
            description = "Identificador único da resposta",
            example = "d1e2f3a4-b5c6-7890-abcd-ef1234567890"
        )
        UUID id,

        @Schema(
            description = "Texto da resposta",
            example = "Ótima pergunta! O evento começa às 19h."
        )
        String answer,

        @Schema(
            description = "Data e hora em que a resposta foi criada",
            example = "2025-06-01T15:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
            description = "Data e hora da última atualização da resposta",
            example = "2025-06-01T16:00:00"
        )
        LocalDateTime updatedAt,

        @Schema(
            description = "Identificador único do usuário autor da resposta",
            example = "a1b2c3d4-e5f6-7890-abcd-111122223333"
        )
        UUID userId,

        @Schema(
            description = "Nome do autor da resposta",
            example = "Maria Silva"
        )
        String userName,

        @Schema(
            description = "URL do avatar do autor da resposta",
            example = "http://localhost:8080/uploads/avatars/uuid.jpg"
        )
        String userAvatarUrl,

        @Schema(
            description = "Identificador único do comentário ao qual esta resposta pertence",
            example = "f0e1d2c3-b4a5-9678-efab-cd1234567890"
        )
        UUID commentId
) {
}
