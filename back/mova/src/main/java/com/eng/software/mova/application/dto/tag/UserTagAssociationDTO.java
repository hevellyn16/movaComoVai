package com.eng.software.mova.application.dto.tag;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record UserTagAssociationDTO(
        Set<@NotNull(message = "Tag IDs cannot be null") UUID> tagIds
) {
}
