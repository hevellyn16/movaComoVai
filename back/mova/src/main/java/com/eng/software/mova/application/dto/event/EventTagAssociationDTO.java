package com.eng.software.mova.application.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record EventTagAssociationDTO(
        @NotNull(message = "A lista de tags não pode ser nula")
        @NotEmpty(message = "A lista de tags não pode estar vazia")
        Set<UUID> tagIds
) {
}
