package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@Schema(description = "IDs das tags a serem associadas a um evento")
public record EventTagAssociationDTO(
        @NotNull(message = "A lista de tags não pode ser nula")
        @NotEmpty(message = "A lista de tags não pode estar vazia")
        @Schema(description = "Conjunto de UUIDs das tags", example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"660e8400-e29b-41d4-a716-446655440000\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<UUID> tagIds
) {
}
