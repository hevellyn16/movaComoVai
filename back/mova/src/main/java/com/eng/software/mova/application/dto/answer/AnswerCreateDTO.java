package com.eng.software.mova.application.dto.answer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload para criação ou atualização de uma resposta a um comentário")
public record AnswerCreateDTO(
        @Schema(
            description = "Texto da resposta. Não pode ser vazio ou conter apenas espaços em branco.",
            example = "Ótima pergunta! O evento começa às 19h.",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Answer must not be blank")
        String answer
) {
}
