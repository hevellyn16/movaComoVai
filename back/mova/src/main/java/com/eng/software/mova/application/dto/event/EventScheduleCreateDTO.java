package com.eng.software.mova.application.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record EventScheduleCreateDTO(
        @NotBlank(message = "O título da programação não pode estar vazio")
        @Length(max = 100, message = "O título não pode exceder 100 caracteres")
        String title,
        String description,
        @NotNull(message = "O horário da programação é obrigatório")
        LocalDateTime scheduleTime
) {
}
