package com.eng.software.mova.application.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para criação de uma nova tag")
public record TagCreateDTO(
        @NotBlank(message = "Tag name cannot be blank")
        @Length(min = 2, max = 100, message = "Tag name must be between 2 and 100 characters")
        @Schema(description = "Nome da tag", example = "Música Ao Vivo", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 100)
        String tagName
) {
}
