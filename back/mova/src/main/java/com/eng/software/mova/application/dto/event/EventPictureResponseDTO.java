package com.eng.software.mova.application.dto.event;

import java.util.UUID;

public record EventPictureResponseDTO(
        UUID id,
        String pictureUrl
) {
}
