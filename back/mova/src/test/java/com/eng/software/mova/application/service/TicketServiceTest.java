package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.ticket.CheckoutDTO;
import com.eng.software.mova.application.dto.ticket.TicketStatusUpdateDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.Ticket;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.TicketRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.factory.TicketFactory;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários do {@link TicketService}.
 * <p>
 * Utiliza Mockito com BDD style (given/when/then) e AssertJ para asserções fluidas.
 * Cada método público do service possui um {@link Nested} group próprio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TicketService — Testes Unitários")
class TicketServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private EventRepositoryPort eventRepositoryPort;

    @InjectMocks
    private TicketService ticketService;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    // ======================== checkout ========================

    @Nested
    @DisplayName("checkout")
    class Checkout {

        @Test
        @DisplayName("Deve criar ticket com taxa de serviço de 10% e status PENDING")
        void shouldCreateTicketWithServiceFeeAndPendingStatus() {
            // given
            CheckoutDTO dto = TicketFactory.createDefaultCheckoutDTO();
            User buyer = TicketFactory.createDefaultBuyer();
            Event event = TicketFactory.createDefaultEvent();

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(dto.eventId())).willReturn(Optional.of(event));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> {
                Ticket saved = inv.getArgument(0);
                saved.setId(TicketFactory.DEFAULT_TICKET_ID);
                return saved;
            });

            // when
            Ticket result = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getUser()).isEqualTo(buyer);
            assertThat(result.getEvent()).isEqualTo(event);
            assertThat(result.getStatus()).isEqualTo(TicketStatus.PENDING);
            assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);

            // Verifica cálculo: 50.00 * 0.10 = 5.00 de taxa, total = 55.00
            assertThat(result.getServiceFee()).isEqualByComparingTo(new BigDecimal("5.00"));
            assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("55.00"));

            assertThat(result.getTicketNumber()).startsWith("TKT-");
            assertThat(result.getQrCodeHash()).isNotBlank();
            assertThat(result.getPurchasedAt()).isNotNull();

            then(ticketRepositoryPort).should().save(any(Ticket.class));
        }

        @Test
        @DisplayName("Deve criar ticket com método de pagamento PIX")
        void shouldCreateTicketWithPix() {
            // given
            CheckoutDTO dto = TicketFactory.createPixCheckoutDTO();
            User buyer = TicketFactory.createDefaultBuyer();
            Event event = TicketFactory.createDefaultEvent();

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(dto.eventId())).willReturn(Optional.of(event));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);

            // then
            assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PIX);
        }

        @Test
        @DisplayName("Deve criar ticket com método de pagamento Boleto")
        void shouldCreateTicketWithBoleto() {
            // given
            CheckoutDTO dto = TicketFactory.createBoletoCheckoutDTO();
            User buyer = TicketFactory.createDefaultBuyer();
            Event event = TicketFactory.createDefaultEvent();

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(dto.eventId())).willReturn(Optional.of(event));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);

            // then
            assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.BOLETO);
        }

        @Test
        @DisplayName("Deve gerar ticketNumber e qrCodeHash únicos a cada chamada")
        void shouldGenerateUniqueTicketNumberAndQrCode() {
            // given
            CheckoutDTO dto = TicketFactory.createDefaultCheckoutDTO();
            User buyer = TicketFactory.createDefaultBuyer();
            Event event = TicketFactory.createDefaultEvent();

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(dto.eventId())).willReturn(Optional.of(event));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result1 = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);
            Ticket result2 = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);

            // then
            assertThat(result1.getTicketNumber()).isNotEqualTo(result2.getTicketNumber());
            assertThat(result1.getQrCodeHash()).isNotEqualTo(result2.getQrCodeHash());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            CheckoutDTO dto = TicketFactory.createDefaultCheckoutDTO();
            UUID userId = UUID.randomUUID();

            given(userRepositoryPort.findById(userId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> ticketService.checkout(dto, userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado");

            then(ticketRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID eventId = UUID.randomUUID();
            CheckoutDTO dto = TicketFactory.createCheckoutDTO(eventId, PaymentMethod.CREDIT_CARD);
            User buyer = TicketFactory.createDefaultBuyer();

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(eventId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Evento não encontrado");

            then(ticketRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Deve calcular taxa de serviço corretamente com preço fracionado")
        void shouldCalculateServiceFeeCorrectlyWithFractionalPrice() {
            // given
            CheckoutDTO dto = TicketFactory.createDefaultCheckoutDTO();
            User buyer = TicketFactory.createDefaultBuyer();
            Event event = TicketFactory.createDefaultEvent();
            event.setPrice(new BigDecimal("33.33")); // 10% = 3.33, total = 36.66

            given(userRepositoryPort.findById(TicketFactory.DEFAULT_USER_ID)).willReturn(Optional.of(buyer));
            given(eventRepositoryPort.findById(dto.eventId())).willReturn(Optional.of(event));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result = ticketService.checkout(dto, TicketFactory.DEFAULT_USER_ID);

            // then
            assertThat(result.getServiceFee()).isEqualByComparingTo(new BigDecimal("3.33"));
            assertThat(result.getTotalPrice()).isEqualByComparingTo(new BigDecimal("36.66"));
        }
    }

    // ======================== findById ========================

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve retornar o ticket quando ele existe")
        void shouldReturnTicketWhenExists() {
            // given
            Ticket ticket = TicketFactory.createDefaultTicket();
            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(ticket));

            // when
            Ticket result = ticketService.findById(TicketFactory.DEFAULT_TICKET_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TicketFactory.DEFAULT_TICKET_ID);
            assertThat(result.getTicketNumber()).isEqualTo(TicketFactory.DEFAULT_TICKET_NUMBER);
            assertThat(result.getStatus()).isEqualTo(TicketStatus.PENDING);

            then(ticketRepositoryPort).should().findById(TicketFactory.DEFAULT_TICKET_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ticket não existe")
        void shouldThrowExceptionWhenTicketNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(ticketRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> ticketService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ingresso não encontrado");
        }
    }

    // ======================== findByUserId ========================

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("Deve retornar página de tickets do usuário")
        void shouldReturnPageOfTicketsForUser() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Ticket ticket1 = TicketFactory.createDefaultTicket();
            Ticket ticket2 = TicketFactory.createPaidTicket();
            ticket2.setId(UUID.randomUUID());
            ticket2.setTicketNumber("TKT-XYZ99999");

            Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket1, ticket2), pageable, 2);

            given(ticketRepositoryPort.findByUserId(TicketFactory.DEFAULT_USER_ID, pageable))
                    .willReturn(ticketPage);

            // when
            Page<Ticket> result = ticketService.findByUserId(TicketFactory.DEFAULT_USER_ID, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);

            then(ticketRepositoryPort).should().findByUserId(TicketFactory.DEFAULT_USER_ID, pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando o usuário não tem tickets")
        void shouldReturnEmptyPageWhenNoTickets() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            UUID userId = UUID.randomUUID();
            Page<Ticket> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(ticketRepositoryPort.findByUserId(userId, pageable)).willReturn(emptyPage);

            // when
            Page<Ticket> result = ticketService.findByUserId(userId, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ======================== updateStatus ========================

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("Deve atualizar status de PENDING para PAID")
        void shouldUpdateStatusToPaid() {
            // given
            Ticket ticket = TicketFactory.createDefaultTicket();
            TicketStatusUpdateDTO dto = TicketFactory.createPaidStatusUpdateDTO();

            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(ticket));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result = ticketService.updateStatus(TicketFactory.DEFAULT_TICKET_ID, dto);

            // then
            assertThat(result.getStatus()).isEqualTo(TicketStatus.PAID);
            assertThat(result.getUpdatedAt()).isNotNull();

            then(ticketRepositoryPort).should().save(ticketCaptor.capture());
            Ticket captured = ticketCaptor.getValue();
            assertThat(captured.getStatus()).isEqualTo(TicketStatus.PAID);
        }

        @Test
        @DisplayName("Deve atualizar status para CANCELLED")
        void shouldUpdateStatusToCancelled() {
            // given
            Ticket ticket = TicketFactory.createDefaultTicket();
            TicketStatusUpdateDTO dto = TicketFactory.createCancelledStatusUpdateDTO();

            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(ticket));
            given(ticketRepositoryPort.save(any(Ticket.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Ticket result = ticketService.updateStatus(TicketFactory.DEFAULT_TICKET_ID, dto);

            // then
            assertThat(result.getStatus()).isEqualTo(TicketStatus.CANCELLED);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ticket não existe")
        void shouldThrowExceptionWhenTicketNotFound() {
            // given
            UUID id = UUID.randomUUID();
            TicketStatusUpdateDTO dto = TicketFactory.createPaidStatusUpdateDTO();

            given(ticketRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> ticketService.updateStatus(id, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ingresso não encontrado");

            then(ticketRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== validate ========================

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        @DisplayName("Deve validar ticket quando status é PAID")
        void shouldValidateTicketWhenPaid() {
            // given
            Ticket paidTicket = TicketFactory.createPaidTicket();

            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(paidTicket));

            // when
            Ticket result = ticketService.validate(TicketFactory.DEFAULT_TICKET_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TicketStatus.PAID);
            assertThat(result.getId()).isEqualTo(TicketFactory.DEFAULT_TICKET_ID);
        }

        @Test
        @DisplayName("Deve lançar IllegalStateException quando status é PENDING")
        void shouldThrowExceptionWhenStatusIsPending() {
            // given
            Ticket pendingTicket = TicketFactory.createDefaultTicket(); // PENDING

            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(pendingTicket));

            // when / then
            assertThatThrownBy(() -> ticketService.validate(TicketFactory.DEFAULT_TICKET_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Apenas ingressos com status PAID podem ser validados");
        }

        @Test
        @DisplayName("Deve lançar IllegalStateException quando status é CANCELLED")
        void shouldThrowExceptionWhenStatusIsCancelled() {
            // given
            Ticket cancelledTicket = TicketFactory.createCancelledTicket();

            given(ticketRepositoryPort.findById(TicketFactory.DEFAULT_TICKET_ID)).willReturn(Optional.of(cancelledTicket));

            // when / then
            assertThatThrownBy(() -> ticketService.validate(TicketFactory.DEFAULT_TICKET_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Apenas ingressos com status PAID podem ser validados");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ticket não existe")
        void shouldThrowExceptionWhenTicketNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(ticketRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> ticketService.validate(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ingresso não encontrado");
        }
    }
}
