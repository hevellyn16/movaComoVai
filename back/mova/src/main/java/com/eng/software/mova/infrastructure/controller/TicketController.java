package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.ticket.CheckoutDTO;
import com.eng.software.mova.application.dto.ticket.TicketResponseDTO;
import com.eng.software.mova.application.dto.ticket.TicketStatusUpdateDTO;
import com.eng.software.mova.application.service.TicketService;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.shared.utils.TicketConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ingressos", description = "Compra, consulta e validação de ingressos para eventos.")
public class TicketController {

    private final TicketService service;

    // ======================== ROTAS DO USUÁRIO ========================

    @Operation(
            summary = "Comprar ingresso (checkout)",
            description = "Inicia a compra de um ingresso para um evento. "
                    + "Gera automaticamente o número do ingresso, QR Code hash e calcula a taxa de serviço. "
                    + "O ingresso é criado com status PENDING. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ingresso criado com sucesso",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (evento ou método de pagamento ausente)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @PostMapping("/checkout")
    public ResponseEntity<TicketResponseDTO> checkout(
            @RequestBody @Valid CheckoutDTO dto,
            @Parameter(hidden = true) @RequestAttribute String userId) {

        Ticket ticket = service.checkout(dto, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketConverter.domainToResponse(ticket));
    }

    @Operation(
            summary = "Listar meus ingressos",
            description = "Retorna uma lista paginada de todos os ingressos comprados pelo usuário autenticado. "
                    + "Exibe o histórico completo da carteira de ingressos. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ingressos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping("/users/me/tickets")
    public ResponseEntity<Page<TicketResponseDTO>> findMyTickets(
            @Parameter(hidden = true) @RequestAttribute String userId,
            Pageable pageable) {

        Page<TicketResponseDTO> tickets = service.findByUserId(UUID.fromString(userId), pageable)
                .map(TicketConverter::domainToResponse);
        return ResponseEntity.ok(tickets);
    }

    // ======================== ROTAS DE CONSULTA (USUÁRIO/ADMIN) ========================

    @Operation(
            summary = "Consultar ingresso por ID",
            description = "Retorna os dados completos de um ingresso específico, incluindo QR Code hash para validação. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingresso encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado", content = @Content)
    })
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponseDTO> findById(
            @Parameter(description = "UUID do ingresso", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(TicketConverter.domainToResponse(service.findById(id)));
    }

    // ======================== ROTAS ADMIN ========================

    @Operation(
            summary = "Atualizar status do ingresso",
            description = "Altera o status de um ingresso (ex: PENDING → PAID, PENDING → CANCELLED). "
                    + "Usado para confirmar pagamentos ou cancelar ingressos. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do ingresso atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado", content = @Content)
    })
    @PatchMapping("/tickets/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @Parameter(description = "UUID do ingresso", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody @Valid TicketStatusUpdateDTO dto) {

        Ticket ticket = service.updateStatus(id, dto);
        return ResponseEntity.ok(TicketConverter.domainToResponse(ticket));
    }

    @Operation(
            summary = "Validar ingresso (leitura de QR Code)",
            description = "Valida um ingresso na entrada do evento, verificando o QR Code hash. "
                    + "Usado pelo staff do evento para confirmar a entrada do participante. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingresso validado com sucesso",
                    content = @Content(schema = @Schema(implementation = TicketResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado", content = @Content)
    })
    @PostMapping("/tickets/{id}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> validate(
            @Parameter(description = "UUID do ingresso a ser validado", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        Ticket ticket = service.validate(id);
        return ResponseEntity.ok(TicketConverter.domainToResponse(ticket));
    }
}
