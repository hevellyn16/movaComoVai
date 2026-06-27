package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Schema(description = "Dados para criação de um item na programação do evento")
public record EventScheduleCreateDTO(
        @NotBlank(message = "O título da programação não pode estar vazio")
        @Length(max = 100, message = "O título não pode exceder 100 caracteres")
        @Schema(description = "Título da atividade/atração", example = "Abertura com DJ Set", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
        String title,

        @Schema(description = "Descrição da atividade", example = "DJ local tocando os melhores hits.")
        String description,

        @NotNull(message = "O horário da programação é obrigatório")
        @Schema(description = "Horário da atividade (ISO 8601)", example = "2025-08-15T19:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime scheduleTime
) {
}
