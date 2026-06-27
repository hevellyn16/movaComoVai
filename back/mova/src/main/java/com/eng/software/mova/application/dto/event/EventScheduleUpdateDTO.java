package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Schema(description = "Dados para atualização de um item da programação. Todos os campos são opcionais.")
public record EventScheduleUpdateDTO(
        @Length(max = 100, message = "O título não pode exceder 100 caracteres")
        @Schema(description = "Novo título da atividade", example = "Show Principal — Banda X", maxLength = 100)
        String title,

        @Schema(description = "Nova descrição da atividade", example = "Show principal da noite com a Banda X.")
        String description,

        @Schema(description = "Novo horário da atividade (ISO 8601)", example = "2025-08-15T21:00:00")
        LocalDateTime scheduleTime
) {
}
