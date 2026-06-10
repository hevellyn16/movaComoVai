package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.ticket.CheckoutDTO;
import com.eng.software.mova.application.dto.ticket.TicketResponseDTO;
import com.eng.software.mova.application.dto.ticket.TicketStatusUpdateDTO;
import com.eng.software.mova.application.service.TicketService;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.shared.utils.TicketConverter;
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
public class TicketController {

    private final TicketService service;

    @PostMapping("/checkout")
    public ResponseEntity<TicketResponseDTO> checkout(
            @RequestBody @Valid CheckoutDTO dto,
            @RequestAttribute String userId) {

        Ticket ticket = service.checkout(dto, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketConverter.domainToResponse(ticket));
    }

    @GetMapping("/users/me/tickets")
    public ResponseEntity<Page<TicketResponseDTO>> findMyTickets(
            @RequestAttribute String userId,
            Pageable pageable) {

        Page<TicketResponseDTO> tickets = service.findByUserId(UUID.fromString(userId), pageable)
                .map(TicketConverter::domainToResponse);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(TicketConverter.domainToResponse(service.findById(id)));
    }

    @PatchMapping("/tickets/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid TicketStatusUpdateDTO dto) {

        Ticket ticket = service.updateStatus(id, dto);
        return ResponseEntity.ok(TicketConverter.domainToResponse(ticket));
    }

    @PostMapping("/tickets/{id}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> validate(@PathVariable UUID id) {
        Ticket ticket = service.validate(id);
        return ResponseEntity.ok(TicketConverter.domainToResponse(ticket));
    }
}
