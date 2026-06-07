package com.eng.software.mova.application.dto.tag;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TagResponseDTO(
        UUID id,
        String tagName
) {
}
