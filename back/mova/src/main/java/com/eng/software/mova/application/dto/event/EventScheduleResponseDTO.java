package com.eng.software.mova.application.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventScheduleResponseDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime scheduleTime
) {
}
