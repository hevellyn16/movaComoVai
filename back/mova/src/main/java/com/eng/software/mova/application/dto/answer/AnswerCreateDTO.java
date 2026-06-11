package com.eng.software.mova.application.dto.answer;

import jakarta.validation.constraints.NotBlank;

public record AnswerCreateDTO(
        @NotBlank(message = "Answer must not be blank")
        String answer
) {
}
