package com.eng.software.mova.application.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String eventName,
        String description,
        String contentRating,
        BigDecimal price,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        UUID creatorId,
        UUID venueId,
        String venueName,
        Set<String> tags
) {
}
