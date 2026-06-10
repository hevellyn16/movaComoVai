package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.domain.port.TicketRepositoryPort;
import com.eng.software.mova.shared.utils.TicketConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TicketRepository implements TicketRepositoryPort {

    private final TicketJpaRepository jpaRepository;

    @Override
    public Ticket save(Ticket ticket) {
        return TicketConverter.entityToDomain(jpaRepository.save(TicketConverter.domainToEntity(ticket)));
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return jpaRepository.findById(id).map(TicketConverter::entityToDomain);
    }

    @Override
    public Page<Ticket> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable).map(TicketConverter::entityToDomain);
    }

    @Override
    public Optional<Ticket> findByQrCodeHash(String qrCodeHash) {
        return jpaRepository.findByQrCodeHash(qrCodeHash).map(TicketConverter::entityToDomain);
    }
}
