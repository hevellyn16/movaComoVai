package com.eng.software.mova.application.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record EventCreateDTO(
        @NotBlank String eventName,
        String description,
        @NotBlank String contentRating,
        @NotNull BigDecimal price,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        UUID venueId,
        Set<UUID> tagIds
) {
}
