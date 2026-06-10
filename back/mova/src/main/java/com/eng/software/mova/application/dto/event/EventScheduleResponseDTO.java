package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de um item da programação do evento")
public record EventScheduleResponseDTO(
        @Schema(description = "UUID do item da programação", example = "880e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Título da atividade", example = "Abertura com DJ Set")
        String title,

        @Schema(description = "Descrição da atividade", example = "DJ local tocando os melhores hits.")
        String description,

        @Schema(description = "Horário da atividade", example = "2025-08-15T19:00:00")
        LocalDateTime scheduleTime
) {
}
