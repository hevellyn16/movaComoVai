package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TicketStatusUpdateDTO(
        @NotNull(message = "O status é obrigatório")
        TicketStatus status
) {
}
