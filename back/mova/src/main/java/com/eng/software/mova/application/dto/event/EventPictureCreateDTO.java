package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "URL de imagem a ser adicionada a um evento")
public record EventPictureCreateDTO(
        @NotBlank(message = "A URL da imagem não pode estar vazia")
        @Length(max = 500, message = "A URL não pode exceder 500 caracteres")
        @Schema(description = "URL da imagem do evento", example = "https://exemplo.com/imagens/evento-rock.jpg", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 500)
        String pictureUrl
) {
}
