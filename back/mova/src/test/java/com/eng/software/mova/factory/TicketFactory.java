package com.eng.software.mova.factory;

import com.eng.software.mova.application.dto.ticket.CheckoutDTO;
import com.eng.software.mova.application.dto.ticket.TicketStatusUpdateDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import com.eng.software.mova.domain.model.enums.UserType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

/**
 * Factory para criação de objetos de teste relacionados a Ticket.
 * Centraliza a construção de entidades de domínio e DTOs,
 * garantindo dados consistentes e reutilizáveis em toda a suíte de testes.
 */
public final class TicketFactory {

    // ======================== IDs PADRÃO ========================

    public static final UUID DEFAULT_TICKET_ID = UUID.fromString("a1a1a1a1-b2b2-c3c3-d4d4-e5e5e5e5e5e5");
    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID DEFAULT_EVENT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    // ======================== CONSTANTES ========================

    public static final String DEFAULT_TICKET_NUMBER = "TKT-ABC12345";
    public static final BigDecimal DEFAULT_EVENT_PRICE = new BigDecimal("50.00");
    public static final BigDecimal DEFAULT_SERVICE_FEE = new BigDecimal("5.00");
    public static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal("55.00");
    public static final String DEFAULT_QR_CODE_HASH = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6";
    public static final LocalDateTime DEFAULT_PURCHASED_AT = LocalDateTime.of(2025, 8, 15, 18, 0, 0);
    public static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.of(2025, 8, 15, 18, 30, 0);

    private TicketFactory() {
        // Utility class — impede instanciação
    }

    // ======================== USER (comprador) ========================

    /**
     * Cria um User comprador padrão.
     */
    public static User createDefaultBuyer() {
        return User.builder()
                .id(DEFAULT_USER_ID)
                .name("João da Silva")
                .username("joaosilva")
                .email("joao@email.com")
                .password("$2a$10$encodedPasswordHash")
                .userType(UserType.COMMON)
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 30, 0))
                .updatedAt(LocalDateTime.of(2025, 6, 10, 14, 0, 0))
                .isActive(true)
                .isPrivate(false)
                .pushNotifications(true)
                .emailNotifications(true)
                .tagsId(new HashSet<>())
                .build();
    }

    // ======================== EVENT ========================

    /**
     * Cria um Event padrão com preço definido para cálculo de taxa.
     */
    public static Event createDefaultEvent() {
        return Event.builder()
                .id(DEFAULT_EVENT_ID)
                .user(createDefaultBuyer())
                .eventName("Show de Rock no Parque")
                .description("Um grande show de rock ao ar livre.")
                .contentRating("Livre")
                .price(DEFAULT_EVENT_PRICE)
                .startsAt(LocalDateTime.of(2025, 8, 15, 19, 0, 0))
                .endsAt(LocalDateTime.of(2025, 8, 15, 23, 0, 0))
                .createdAt(LocalDateTime.of(2025, 7, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 10, 14, 0, 0))
                .build();
    }

    // ======================== TICKET DOMAIN ========================

    /**
     * Cria um Ticket de domínio padrão com status PENDING.
     */
    public static Ticket createDefaultTicket() {
        return Ticket.builder()
                .id(DEFAULT_TICKET_ID)
                .user(createDefaultBuyer())
                .event(createDefaultEvent())
                .ticketNumber(DEFAULT_TICKET_NUMBER)
                .status(TicketStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .totalPrice(DEFAULT_TOTAL_PRICE)
                .serviceFee(DEFAULT_SERVICE_FEE)
                .qrCodeHash(DEFAULT_QR_CODE_HASH)
                .purchasedAt(DEFAULT_PURCHASED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .build();
    }

    /**
     * Cria um Ticket com status PAID.
     */
    public static Ticket createPaidTicket() {
        Ticket ticket = createDefaultTicket();
        ticket.setStatus(TicketStatus.PAID);
        return ticket;
    }

    /**
     * Cria um Ticket com status CANCELLED.
     */
    public static Ticket createCancelledTicket() {
        Ticket ticket = createDefaultTicket();
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticket;
    }

    /**
     * Cria um Ticket com método de pagamento PIX.
     */
    public static Ticket createPixTicket() {
        Ticket ticket = createDefaultTicket();
        ticket.setPaymentMethod(PaymentMethod.PIX);
        return ticket;
    }

    // ======================== CHECKOUT DTO ========================

    /**
     * Cria um CheckoutDTO padrão com cartão de crédito.
     */
    public static CheckoutDTO createDefaultCheckoutDTO() {
        return new CheckoutDTO(DEFAULT_EVENT_ID, PaymentMethod.CREDIT_CARD);
    }

    /**
     * Cria um CheckoutDTO com PIX.
     */
    public static CheckoutDTO createPixCheckoutDTO() {
        return new CheckoutDTO(DEFAULT_EVENT_ID, PaymentMethod.PIX);
    }

    /**
     * Cria um CheckoutDTO com Boleto.
     */
    public static CheckoutDTO createBoletoCheckoutDTO() {
        return new CheckoutDTO(DEFAULT_EVENT_ID, PaymentMethod.BOLETO);
    }

    /**
     * Cria um CheckoutDTO com evento customizado.
     */
    public static CheckoutDTO createCheckoutDTO(UUID eventId, PaymentMethod paymentMethod) {
        return new CheckoutDTO(eventId, paymentMethod);
    }

    // ======================== STATUS UPDATE DTO ========================

    /**
     * Cria um TicketStatusUpdateDTO para PAID.
     */
    public static TicketStatusUpdateDTO createPaidStatusUpdateDTO() {
        return new TicketStatusUpdateDTO(TicketStatus.PAID);
    }

    /**
     * Cria um TicketStatusUpdateDTO para CANCELLED.
     */
    public static TicketStatusUpdateDTO createCancelledStatusUpdateDTO() {
        return new TicketStatusUpdateDTO(TicketStatus.CANCELLED);
    }

    /**
     * Cria um TicketStatusUpdateDTO com status customizado.
     */
    public static TicketStatusUpdateDTO createStatusUpdateDTO(TicketStatus status) {
        return new TicketStatusUpdateDTO(status);
    }
}
