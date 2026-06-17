package com.eng.software.mova.application.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload para criação ou atualização de um comentário em um evento")
public record CommentCreateDTO(
        @Schema(
            description = "Texto do comentário. Não pode ser vazio ou conter apenas espaços em branco.",
            example = "Que evento incrível! Mal posso esperar.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Content must not be blank")
        String content
) {
}
