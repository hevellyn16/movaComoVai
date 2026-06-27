package com.eng.software.mova.domain.model;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {
    @EqualsAndHashCode.Include
    private UUID id;
    private User user;
    private Event event;
    private String ticketNumber;
    private TicketStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal totalPrice;
    private BigDecimal serviceFee;
    private String qrCodeHash;
    private LocalDateTime purchasedAt;
    private LocalDateTime updatedAt;
}
