package com.eng.software.mova.application.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDTO(
        @NotBlank(message = "Content must not be blank")
        String content
) {
}
