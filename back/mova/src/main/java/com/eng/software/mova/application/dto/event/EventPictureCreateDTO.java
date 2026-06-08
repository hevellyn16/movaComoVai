package com.eng.software.mova.application.dto.event;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EventPictureCreateDTO(
        @NotBlank(message = "A URL da imagem não pode estar vazia")
        @Length(max = 500, message = "A URL não pode exceder 500 caracteres")
        String pictureUrl
) {
}
