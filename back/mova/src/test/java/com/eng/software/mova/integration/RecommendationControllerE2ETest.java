package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.infrastructure.persistence.repository.EventJpaRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class RecommendationControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, events CASCADE");
    }

    // ======================== GET /recommendations/feed ========================

    @Nested
    @DisplayName("GET /recommendations/feed")
    class GetFeedTests {

        @Test
        @DisplayName("Deve retornar o feed de recomendações do usuário com sucesso")
        void shouldReturnFeedSuccessfully() throws Exception {
            UserEntity user = createCommonUser("Leitor", "leitor", "leitor@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            createEvent(user, venue, "Evento Recomendado");

            mockMvc.perform(get("/recommendations/feed?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/recommendations/feed"))
                    .andExpect(status().isUnauthorized());
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
