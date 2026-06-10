package com.eng.software.mova.application.dto.picture;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentPictureResponseDTO(
        UUID id,
        UUID commentId,
        String pictureUrl
) {
}

