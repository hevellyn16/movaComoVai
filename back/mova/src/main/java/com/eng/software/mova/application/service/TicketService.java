package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.ticket.CheckoutDTO;
import com.eng.software.mova.application.dto.ticket.TicketStatusUpdateDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.TicketRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepositoryPort ticketRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final EventRepositoryPort eventRepositoryPort;

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.10");

    @Transactional
    public Ticket checkout(CheckoutDTO dto, UUID userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Event event = eventRepositoryPort.findById(dto.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        BigDecimal serviceFee = event.getPrice().multiply(SERVICE_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = event.getPrice().add(serviceFee);

        String ticketNumber = generateTicketNumber();
        String qrCodeHash = generateQrCodeHash();

        Ticket ticket = Ticket.builder()
                .user(user)
                .event(event)
                .ticketNumber(ticketNumber)
                .status(TicketStatus.PENDING)
                .paymentMethod(dto.paymentMethod())
                .totalPrice(totalPrice)
                .serviceFee(serviceFee)
                .qrCodeHash(qrCodeHash)
                .purchasedAt(LocalDateTime.now())
                .build();

        return ticketRepositoryPort.save(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket findById(UUID id) {
        return ticketRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado."));
    }

    @Transactional(readOnly = true)
    public Page<Ticket> findByUserId(UUID userId, Pageable pageable) {
        return ticketRepositoryPort.findByUserId(userId, pageable);
    }

    @Transactional
    public Ticket updateStatus(UUID id, TicketStatusUpdateDTO dto) {
        Ticket ticket = findById(id);
        ticket.setStatus(dto.status());
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepositoryPort.save(ticket);
    }

    @Transactional
    public Ticket validate(UUID id) {
        Ticket ticket = findById(id);

        if (ticket.getStatus() != TicketStatus.PAID) {
            throw new IllegalStateException("Apenas ingressos com status PAID podem ser validados.");
        }

        return ticket;
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateQrCodeHash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
