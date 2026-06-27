package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

    Page<TicketEntity> findByUserId(UUID userId, Pageable pageable);

    Optional<TicketEntity> findByQrCodeHash(String qrCodeHash);
}
