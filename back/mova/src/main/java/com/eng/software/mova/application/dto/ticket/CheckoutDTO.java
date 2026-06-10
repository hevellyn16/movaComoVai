package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutDTO(
        @NotNull(message = "O ID do evento é obrigatório")
        UUID eventId,
        @NotNull(message = "O método de pagamento é obrigatório")
        PaymentMethod paymentMethod
) {
}
