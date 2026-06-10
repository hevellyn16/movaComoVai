package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponseDTO(
        UUID id,
        UUID userId,
        UUID eventId,
        String eventName,
        String ticketNumber,
        TicketStatus status,
        PaymentMethod paymentMethod,
        BigDecimal totalPrice,
        BigDecimal serviceFee,
        String qrCodeHash,
        LocalDateTime purchasedAt,
        LocalDateTime updatedAt
) {
}
