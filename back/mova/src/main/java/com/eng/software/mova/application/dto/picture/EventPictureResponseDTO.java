package com.eng.software.mova.application.dto.picture;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EventPictureResponseDTO(
        UUID id,
        UUID eventId,
        String pictureUrl
) {
}
