package com.eng.software.mova.application.dto.tag;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TagUpdateDTO(
        @NotBlank(message = "Tag name cannot be blank")
        @Length(min = 2, max = 100, message = "Tag name must be between 2 and 100 characters")
        String tagName
) {
}
