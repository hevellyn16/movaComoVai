package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.ticket.TicketResponseDTO;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.infrastructure.persistence.entity.TicketEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketConverter {

    public static Ticket entityToDomain(TicketEntity entity) {
        if (entity == null) return null;
        return Ticket.builder()
                .id(entity.getId())
                .user(UserConverter.entityToDomain(entity.getUser()))
                .event(EventConverter.entityToDomain(entity.getEvent()))
                .ticketNumber(entity.getTicketNumber())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .totalPrice(entity.getTotalPrice())
                .serviceFee(entity.getServiceFee())
                .qrCodeHash(entity.getQrCodeHash())
                .purchasedAt(entity.getPurchasedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static TicketEntity domainToEntity(Ticket domain) {
        if (domain == null) return null;
        return TicketEntity.builder()
                .id(domain.getId())
                .user(UserConverter.domainToEntity(domain.getUser()))
                .event(EventConverter.domainToEntity(domain.getEvent()))
                .ticketNumber(domain.getTicketNumber())
                .status(domain.getStatus())
                .paymentMethod(domain.getPaymentMethod())
                .totalPrice(domain.getTotalPrice())
                .serviceFee(domain.getServiceFee())
                .qrCodeHash(domain.getQrCodeHash())
                .purchasedAt(domain.getPurchasedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static TicketResponseDTO domainToResponse(Ticket domain) {
        if (domain == null) return null;
        return new TicketResponseDTO(
                domain.getId(),
                domain.getUser() != null ? domain.getUser().getId() : null,
                domain.getEvent() != null ? domain.getEvent().getId() : null,
                domain.getEvent() != null ? domain.getEvent().getEventName() : null,
                domain.getTicketNumber(),
                domain.getStatus(),
                domain.getPaymentMethod(),
                domain.getTotalPrice(),
                domain.getServiceFee(),
                domain.getQrCodeHash(),
                domain.getPurchasedAt(),
                domain.getUpdatedAt()
        );
    }
}
