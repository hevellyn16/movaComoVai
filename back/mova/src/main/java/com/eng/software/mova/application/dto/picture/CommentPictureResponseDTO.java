package com.eng.software.mova.application.dto.picture;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Dados retornados ao consultar ou manipular uma imagem vinculada a um comentário")
public record CommentPictureResponseDTO(
        @Schema(
            description = "Identificador único da imagem do comentário",
            example = "e1f2a3b4-c5d6-7890-abcd-ef1234567890"
        )
        UUID id,

        @Schema(
            description = "Identificador único do comentário ao qual a imagem está vinculada",
            example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
        )
        UUID commentId,

        @Schema(
            description = "URL pública de acesso à imagem armazenada",
            example = "https://storage.example.com/comments/imagem01.jpg"
        )
        String pictureUrl
) {
}
