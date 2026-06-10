package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados para criação de um novo evento")
public record EventCreateDTO(
        @NotBlank
        @Schema(description = "Nome do evento", example = "Show de Rock no Parque", requiredMode = Schema.RequiredMode.REQUIRED)
        String eventName,

        @Schema(description = "Descrição detalhada do evento", example = "Um grande show de rock ao ar livre com bandas locais e nacionais.")
        String description,

        @NotBlank
        @Schema(description = "Classificação indicativa do evento", example = "Livre", requiredMode = Schema.RequiredMode.REQUIRED)
        String contentRating,

        @NotNull
        @Schema(description = "Preço do ingresso em reais", example = "50.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @NotNull
        @Schema(description = "Data e hora de início do evento (ISO 8601)", example = "2025-08-15T19:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startsAt,

        @NotNull
        @Schema(description = "Data e hora de término do evento (ISO 8601)", example = "2025-08-15T23:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime endsAt,

        @Schema(description = "UUID do local (venue) onde o evento será realizado", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID venueId,

        @Schema(description = "IDs das tags a serem associadas ao evento", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        Set<UUID> tagIds
) {
}
