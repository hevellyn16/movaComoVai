package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Payload para criação de um novo evento")
public record EventCreateDTO(
        @Schema(
            description = "Nome do evento. Não pode ser vazio.",
            example = "Festival de Jazz na Praça",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String eventName,

        @Schema(
            description = "Descrição detalhada do evento",
            example = "Uma noite incrível de jazz ao ar livre com bandas locais."
        )
        String description,

        @Schema(
            description = "Classificação indicativa de conteúdo do evento (ex: 'livre', '12', '16', '18'). Não pode ser vazio.",
            example = "livre",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String contentRating,

        @Schema(
            description = "Preço de entrada do evento. Use 0.00 para eventos gratuitos. Não pode ser nulo.",
            example = "25.00",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull BigDecimal price,

        @Schema(
            description = "Data e hora de início do evento no formato ISO 8601. Não pode ser nulo.",
            example = "2025-07-10T19:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull LocalDateTime startsAt,

        @Schema(
            description = "Data e hora de encerramento do evento no formato ISO 8601. Não pode ser nulo.",
            example = "2025-07-10T23:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull LocalDateTime endsAt,

        @Schema(
            description = "Identificador único (UUID) do local onde o evento será realizado. Opcional.",
            example = "c3d4e5f6-a7b8-9012-cdef-123456789abc"
        )
        UUID venueId,

        @Schema(
            description = "Conjunto de identificadores únicos (UUIDs) das tags associadas ao evento. Opcional.",
            example = "[\"d1e2f3a4-b5c6-7890-abcd-111111111111\", \"e2f3a4b5-c6d7-8901-bcde-222222222222\"]"
        )
        Set<UUID> tagIds
) {
}
