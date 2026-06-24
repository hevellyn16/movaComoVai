package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados completos de um evento (resposta)")
public record EventResponseDTO(
        @Schema(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome do evento", example = "Show de Rock no Parque")
        String eventName,

        @Schema(description = "Descrição detalhada do evento", example = "Um grande show de rock ao ar livre.")
        String description,

        @Schema(description = "Classificação indicativa", example = "Livre")
        String contentRating,

        @Schema(description = "Preço do ingresso em reais", example = "50.00")
        BigDecimal price,

        @Schema(description = "Data e hora de início", example = "2025-08-15T19:00:00")
        LocalDateTime startsAt,

        @Schema(description = "Data e hora de término", example = "2025-08-15T23:00:00")
        LocalDateTime endsAt,

        @Schema(description = "UUID do usuário criador do evento", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID creatorId,

        @Schema(description = "UUID do local do evento", example = "660e8400-e29b-41d4-a716-446655440000")
        UUID venueId,

        @Schema(description = "Nome do local do evento", example = "Parque Ibirapuera")
        String venueName,

        @Schema(description = "Tags associadas ao evento", example = "[\"Rock\", \"Música\", \"Ao vivo\"]")
        Set<String> tags,

        @Schema(description = "Itens da programação do evento")
        List<EventScheduleResponseDTO> schedules,

        Set<UUID> likedByUserIds,

        Set<UUID> favoritedByUserIds
) {
}
