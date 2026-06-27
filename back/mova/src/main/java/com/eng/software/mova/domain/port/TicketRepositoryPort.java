package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID id);
    Page<Ticket> findByUserId(UUID userId, Pageable pageable);
    Optional<Ticket> findByQrCodeHash(String qrCodeHash);
}
