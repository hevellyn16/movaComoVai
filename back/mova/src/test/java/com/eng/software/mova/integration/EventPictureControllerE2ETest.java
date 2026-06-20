package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.infrastructure.persistence.repository.EventJpaRepository;
import com.eng.software.mova.infrastructure.persistence.repository.EventPictureJpaRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class EventPictureControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private EventPictureJpaRepository eventPictureJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private FileStoragePort fileStoragePort;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, events, event_pictures CASCADE");
    }

    // ======================== GET /event-pictures/{id} ========================

    @Nested
    @DisplayName("GET /event-pictures/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar imagem de evento por ID retornando 200")
        void shouldFindByIdSuccessfully() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");
            EventPictureEntity picture = createEventPicture(event, "https://storage.example.com/pic.jpg");

            mockMvc.perform(get("/event-pictures/{id}", picture.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(picture.getId().toString()))
                    .andExpect(jsonPath("$.eventId").value(event.getId().toString()))
                    .andExpect(jsonPath("$.pictureUrl").value("https://storage.example.com/pic.jpg"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(get("/event-pictures/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/event-pictures/{id}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /event-pictures/events/{eventId} ========================

    @Nested
    @DisplayName("GET /event-pictures/events/{eventId}")
    class FindByEventIdTests {

        @Test
        @DisplayName("Deve listar imagens de um evento retornando 200")
        void shouldListPicturesForEvent() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");
            createEventPicture(event, "https://storage.example.com/pic1.jpg");
            createEventPicture(event, "https://storage.example.com/pic2.jpg");

            mockMvc.perform(get("/event-pictures/events/{eventId}?page=0&size=10", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar página vazia se evento não tiver imagens")
        void shouldReturnEmptyPageWhenNoPictures() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");

            mockMvc.perform(get("/event-pictures/events/{eventId}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/event-pictures/events/{eventId}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== POST /event-pictures/{eventId} ========================

    @Nested
    @DisplayName("POST /event-pictures/{eventId}")
    class UploadPictureTests {

        @Test
        @DisplayName("Deve fazer upload de imagem com sucesso retornando 201")
        void shouldUploadPictureSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");

            when(fileStoragePort.uploadFile(any())).thenReturn("https://storage.example.com/uploaded.jpg");

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "image.jpg",
                    "image/jpeg",
                    "dummy image content".getBytes()
            );

            mockMvc.perform(multipart("/event-pictures/{eventId}", event.getId())
                            .file(file)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.eventId").value(event.getId().toString()))
                    .andExpect(jsonPath("$.pictureUrl").value("https://storage.example.com/uploaded.jpg"));

            assertThat(eventPictureJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 404 quando evento não existe")
        void shouldReturnNotFoundWhenEventDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "image.jpg",
                    "image/jpeg",
                    "dummy image content".getBytes()
            );

            mockMvc.perform(multipart("/event-pictures/{eventId}", UUID.randomUUID())
                            .file(file)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "image.jpg",
                    "image/jpeg",
                    "dummy".getBytes()
            );

            mockMvc.perform(multipart("/event-pictures/{eventId}", UUID.randomUUID())
                            .file(file))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== DELETE /event-pictures/{id} ========================

    @Nested
    @DisplayName("DELETE /event-pictures/{id}")
    class DeletePictureTests {

        @Test
        @DisplayName("Deve excluir imagem com sucesso retornando 204")
        void shouldDeletePictureSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Show de Rock");
            EventPictureEntity picture = createEventPicture(event, "https://storage.example.com/pic.jpg");

            mockMvc.perform(delete("/event-pictures/{id}", picture.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(eventPictureJpaRepository.findById(picture.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar 404 quando imagem não existe")
        void shouldReturnNotFoundWhenPictureDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(delete("/event-pictures/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/event-pictures/{id}", UUID.randomUUID()))
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

    private EventPictureEntity createEventPicture(EventEntity event, String url) {
        return eventPictureJpaRepository.saveAndFlush(
                EventPictureEntity.builder()
                        .event(event)
                        .pictureUrl(url)
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
