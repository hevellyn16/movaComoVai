package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados completos de um ingresso (resposta)")
public record TicketResponseDTO(
        @Schema(description = "UUID do ingresso", example = "110e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "UUID do usuário dono do ingresso", example = "220e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "UUID do evento associado", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID eventId,

        @Schema(description = "Nome do evento associado", example = "Show de Rock no Parque")
        String eventName,

        @Schema(description = "Número único do ingresso", example = "TKT-A1B2C3D4")
        String ticketNumber,

        @Schema(description = "Status atual do ingresso", example = "PAID")
        TicketStatus status,

        @Schema(description = "Método de pagamento utilizado", example = "PIX")
        PaymentMethod paymentMethod,

        @Schema(description = "Preço total pago pelo ingresso (ingresso + taxa)", example = "55.00")
        BigDecimal totalPrice,

        @Schema(description = "Taxa de serviço aplicada (10%)", example = "5.00")
        BigDecimal serviceFee,

        @Schema(description = "Hash do QR Code usado para validação na entrada", example = "hash123456789")
        String qrCodeHash,

        @Schema(description = "Data e hora em que o ingresso foi comprado", example = "2025-06-10T15:30:00")
        LocalDateTime purchasedAt,

        @Schema(description = "Data e hora da última atualização do ingresso", example = "2025-06-10T15:30:00")
        LocalDateTime updatedAt
) {
}
