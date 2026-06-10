package com.eng.software.mova.application.dto.ticket;

import com.eng.software.mova.domain.model.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para atualização de status de um ingresso")
public record TicketStatusUpdateDTO(
        @NotNull(message = "O status é obrigatório")
        @Schema(description = "Novo status do ingresso", example = "CANCELLED", requiredMode = Schema.RequiredMode.REQUIRED)
        TicketStatus status
) {
}
