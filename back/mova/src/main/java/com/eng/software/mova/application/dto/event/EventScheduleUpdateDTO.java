package com.eng.software.mova.application.dto.event;

import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record EventScheduleUpdateDTO(
        @Length(max = 100, message = "O título não pode exceder 100 caracteres")
        String title,
        String description,
        LocalDateTime scheduleTime
) {
}
