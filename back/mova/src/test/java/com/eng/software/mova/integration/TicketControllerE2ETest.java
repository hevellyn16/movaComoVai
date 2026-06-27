package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.PaymentMethod;
import com.eng.software.mova.domain.model.enums.TicketStatus;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.TicketEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.infrastructure.persistence.repository.EventJpaRepository;
import com.eng.software.mova.infrastructure.persistence.repository.TicketJpaRepository;
import com.eng.software.mova.infrastructure.persistence.repository.UserJpaRepository;
import com.eng.software.mova.infrastructure.persistence.repository.VenueJpaRepository;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class TicketControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TicketJpaRepository ticketJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, events, tickets CASCADE");
    }

    // ======================== POST /checkout ========================

    @Nested
    @DisplayName("POST /checkout")
    class CheckoutTests {

        @Test
        @DisplayName("Deve fazer checkout de ingresso retornando 201")
        void shouldCheckoutSuccessfully() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");

            String jsonPayload = """
                {
                    "eventId": "%s",
                    "paymentMethod": "CREDIT_CARD"
                }
                """.formatted(event.getId());

            mockMvc.perform(post("/checkout")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload)
                            .requestAttr("userId", user.getId().toString()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"));

            assertThat(ticketJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(post("/checkout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /users/me/tickets ========================

    @Nested
    @DisplayName("GET /users/me/tickets")
    class FindMyTicketsTests {

        @Test
        @DisplayName("Deve listar os ingressos do usuário paginados retornando 200")
        void shouldListMyTickets() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");

            createTicket(user, event, TicketStatus.PAID);
            createTicket(user, event, TicketStatus.PENDING);

            mockMvc.perform(get("/users/me/tickets?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .requestAttr("userId", user.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/users/me/tickets"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /tickets/{id} ========================

    @Nested
    @DisplayName("GET /tickets/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar ingresso por ID retornando 200")
        void shouldFindTicketById() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");

            TicketEntity ticket = createTicket(user, event, TicketStatus.PAID);

            mockMvc.perform(get("/tickets/{id}", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ticket.getId().toString()))
                    .andExpect(jsonPath("$.status").value("PAID"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(get("/tickets/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== PATCH /tickets/{id}/status ========================

    @Nested
    @DisplayName("PATCH /tickets/{id}/status")
    class UpdateStatusTests {

        @Test
        @DisplayName("Deve atualizar status do ingresso como ADMIN retornando 200")
        void shouldUpdateTicketStatusAsAdmin() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Show de Rock");

            TicketEntity ticket = createTicket(admin, event, TicketStatus.PENDING);

            String jsonPayload = """
                {
                    "status": "PAID"
                }
                """;

            mockMvc.perform(patch("/tickets/{id}/status", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PAID"));

            TicketEntity updated = ticketJpaRepository.findById(ticket.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(TicketStatus.PAID);
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            String jsonPayload = """
                {
                    "status": "PAID"
                }
                """;

            mockMvc.perform(patch("/tickets/{id}/status", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== POST /tickets/{id}/validate ========================

    @Nested
    @DisplayName("POST /tickets/{id}/validate")
    class ValidateTicketTests {

        @Test
        @DisplayName("Deve validar ingresso como ADMIN retornando 200")
        void shouldValidateTicketAsAdmin() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Show de Rock");

            TicketEntity ticket = createTicket(admin, event, TicketStatus.PAID);

            mockMvc.perform(post("/tickets/{id}/validate", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PAID"));

            TicketEntity updated = ticketJpaRepository.findById(ticket.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(TicketStatus.PAID);
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            mockMvc.perform(post("/tickets/{id}/validate", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== HELPER METHODS ========================

    private VenueEntity createVenue(String name, String number, String city) {
        return venueJpaRepository.saveAndFlush(
                VenueEntity.builder()
                        .name(name)
                        .number(number)
                        .city(city)
                        .street("Rua X")
                        .neighborhood("Bairro Y")
                        .hasParkingLot(false)
                        .hasAccessibility(false)
                        .hasBathroom(false)
                        .hasFoodsAndDrinks(false)
                        .build());
    }

    private EventEntity createEvent(UserEntity creator, VenueEntity venue, String name) {
        return eventJpaRepository.saveAndFlush(
                EventEntity.builder()
                        .user(creator)
                        .venue(venue)
                        .eventName(name)
                        .description("Descrição do evento")
                        .contentRating("Livre")
                        .price(new BigDecimal("100.00"))
                        .startsAt(LocalDateTime.now().plusDays(1))
                        .endsAt(LocalDateTime.now().plusDays(2))
                        .build()
        );
    }

    private TicketEntity createTicket(UserEntity user, EventEntity event, TicketStatus status) {
        return ticketJpaRepository.saveAndFlush(
                TicketEntity.builder()
                        .user(user)
                        .event(event)
                        .ticketNumber(UUID.randomUUID().toString())
                        .status(status)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .totalPrice(new BigDecimal("110.00"))
                        .serviceFee(new BigDecimal("10.00"))
                        .qrCodeHash(UUID.randomUUID().toString())
                        .build()
        );
    }

    private UserEntity createCommonUser(String name, String username, String email, String rawPassword) {
        return userJpaRepository.saveAndFlush(
                UserEntity.builder()
                        .name(name)
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .userType(UserType.COMMON)
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .isPrivate(false)
                        .pushNotifications(true)
                        .emailNotifications(true)
                        .build());
    }

    private UserEntity createAdminUser(String name, String username, String email, String rawPassword) {
        return userJpaRepository.saveAndFlush(
                UserEntity.builder()
                        .name(name)
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .userType(UserType.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .isPrivate(false)
                        .pushNotifications(true)
                        .emailNotifications(true)
                        .build());
    }

    private String getAccessToken(UserEntity user) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getUserType().name())
                .build();
        return jwtTokenProvider.generateToken(userDetails);
    }
}
