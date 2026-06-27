package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dados para compra de ingresso (checkout)")
public record CheckoutDTO(
        @NotNull(message = "O ID do evento é obrigatório")
        @Schema(description = "UUID do evento para o qual o ingresso está sendo comprado", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID eventId,

        @NotNull(message = "O método de pagamento é obrigatório")
        @Schema(description = "Método de pagamento escolhido", example = "CREDIT_CARD", requiredMode = Schema.RequiredMode.REQUIRED)
        PaymentMethod paymentMethod
) {
}
