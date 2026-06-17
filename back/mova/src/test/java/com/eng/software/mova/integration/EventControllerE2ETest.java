package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.*;
import com.eng.software.mova.infrastructure.persistence.repository.*;
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
class EventControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private TagJpaRepository tagJpaRepository;
    @Autowired private EventPictureJpaRepository eventPictureJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, tags, events, event_pictures, event_schedules CASCADE");
    }

    // ======================== GET /events ========================

    @Nested
    @DisplayName("GET /events")
    class FindAllTests {

        @Test
        @DisplayName("Deve listar todos os eventos")
        void shouldFindAllEvents() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            createEvent(user, venue, "Evento 1");
            createEvent(user, venue, "Evento 2");

            mockMvc.perform(get("/events?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(2));
        }
    }

    // ======================== GET /events/{id} ========================

    @Nested
    @DisplayName("GET /events/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar evento por ID")
        void shouldFindEventById() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Evento Show");

            mockMvc.perform(get("/events/{id}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.eventName").value("Evento Show"));
        }
    }

    // ======================== POST /events (ADMIN) ========================

    @Nested
    @DisplayName("POST /events")
    class CreateEventTests {

        @Test
        @DisplayName("Deve criar evento com sucesso como ADMIN")
        void shouldCreateEventAsAdmin() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Local", "123", "Cidade");

            String jsonPayload = """
                {
                    "eventName": "Novo Evento",
                    "description": "Descricao",
                    "contentRating": "Livre",
                    "price": 100.00,
                    "startsAt": "2025-10-10T20:00:00",
                    "endsAt": "2025-10-10T23:00:00",
                    "venueId": "%s"
                }
                """.formatted(venue.getId());

            mockMvc.perform(post("/events")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload)
                            .requestAttr("userId", admin.getId().toString()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.eventName").value("Novo Evento"));

            assertThat(eventJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            mockMvc.perform(post("/events")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== PUT /events/{id} (ADMIN) ========================

    @Nested
    @DisplayName("PUT /events/{id}")
    class UpdateEventTests {

        @Test
        @DisplayName("Deve atualizar evento com sucesso como ADMIN")
        void shouldUpdateEventAsAdmin() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento Antigo");

            String jsonPayload = """
                {
                    "eventName": "Evento Atualizado"
                }
                """;

            mockMvc.perform(put("/events/{id}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.eventName").value("Evento Atualizado"));
        }
    }

    // ======================== DELETE /events/{id} (ADMIN) ========================

    @Nested
    @DisplayName("DELETE /events/{id}")
    class DeleteEventTests {

        @Test
        @DisplayName("Deve excluir evento com sucesso como ADMIN")
        void shouldDeleteEventAsAdmin() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");

            mockMvc.perform(delete("/events/{id}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(eventJpaRepository.findById(event.getId())).isEmpty();
        }
    }

    // ======================== INTERAÇÕES COM USUÁRIO ========================

    @Nested
    @DisplayName("Interações de Usuário (Likes e Favorites)")
    class UserInteractionsTests {

        @Test
        @DisplayName("Deve curtir evento")
        void shouldLikeEvent() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Evento");

            mockMvc.perform(post("/events/{eventId}/likes", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve favoritar evento")
        void shouldFavoriteEvent() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Evento");

            mockMvc.perform(post("/events/{eventId}/favorites", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve remover curtida do evento")
        void shouldUnlikeEvent() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Evento");
            event.getLikedByUsers().add(user);
            eventJpaRepository.saveAndFlush(event);

            mockMvc.perform(delete("/events/{eventId}/likes", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve remover favorito do evento")
        void shouldRemoveFavorite() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(user, venue, "Evento");
            event.getFavoriteByUsers().add(user);
            eventJpaRepository.saveAndFlush(event);

            mockMvc.perform(delete("/events/{eventId}/favorites", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    // ======================== TAGS DO EVENTO (ADMIN) ========================

    @Nested
    @DisplayName("Tags do Evento (ADMIN)")
    class EventTagsTests {

        @Test
        @DisplayName("Deve adicionar tag ao evento como ADMIN")
        void shouldAddTagToEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");
            TagEntity tag = createTag("Música");

            String jsonPayload = """
                {
                    "tagIds": ["%s"]
                }
                """.formatted(tag.getId());

            mockMvc.perform(post("/events/{eventId}/tags", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve remover tag do evento como ADMIN")
        void shouldRemoveTagFromEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");
            TagEntity tag = createTag("Música");
            event.getTags().add(tag);
            eventJpaRepository.saveAndFlush(event);

            mockMvc.perform(delete("/events/{eventId}/tags/{tagId}", event.getId(), tag.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
        }
    }

    // ======================== IMAGENS DO EVENTO (ADMIN) ========================

    @Nested
    @DisplayName("Imagens do Evento (ADMIN)")
    class EventPicturesTests {

        @Test
        @DisplayName("Deve adicionar imagem ao evento como ADMIN")
        void shouldAddPictureToEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");

            String jsonPayload = """
                {
                    "pictureUrl": "https://storage.example.com/pic.jpg"
                }
                """;

            mockMvc.perform(post("/events/{eventId}/pictures", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve remover imagem do evento como ADMIN")
        void shouldRemovePictureFromEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");
            
            EventPictureEntity picture = eventPictureJpaRepository.saveAndFlush(EventPictureEntity.builder()
                    .event(event)
                    .pictureUrl("https://storage.example.com/pic.jpg")
                    .build());

            mockMvc.perform(delete("/events/{eventId}/pictures/{pictureId}", event.getId(), picture.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
        }
    }

    // ======================== PROGRAMAÇÃO DO EVENTO (ADMIN) ========================

    @Nested
    @DisplayName("Programação do Evento (ADMIN)")
    class EventSchedulesTests {

        @Test
        @DisplayName("Deve adicionar schedule ao evento como ADMIN")
        void shouldAddScheduleToEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");

            String jsonPayload = """
                {
                    "title": "Abertura",
                    "description": "Portões abertos",
                    "scheduleTime": "2025-10-10T19:00:00"
                }
                """;

            mockMvc.perform(post("/events/{eventId}/schedules", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve atualizar schedule do evento como ADMIN")
        void shouldUpdateScheduleOfEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");

            UUID scheduleId = UUID.randomUUID();
            jdbcTemplate.update(
                    "INSERT INTO event_schedules (id, event_id, title, schedule_time) VALUES (?, ?, ?, ?)",
                    scheduleId, event.getId(), "Abertura", LocalDateTime.now().plusDays(1)
            );

            String jsonPayload = """
                {
                    "title": "Abertura Atualizada"
                }
                """;

            mockMvc.perform(put("/events/{eventId}/schedules/{scheduleId}", event.getId(), scheduleId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve remover schedule do evento como ADMIN")
        void shouldRemoveScheduleFromEvent() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);
            VenueEntity venue = createVenue("Local", "123", "Cidade");
            EventEntity event = createEvent(admin, venue, "Evento");

            UUID scheduleId = UUID.randomUUID();
            jdbcTemplate.update(
                    "INSERT INTO event_schedules (id, event_id, title, schedule_time) VALUES (?, ?, ?, ?)",
                    scheduleId, event.getId(), "Abertura", LocalDateTime.now().plusDays(1)
            );

            mockMvc.perform(delete("/events/{eventId}/schedules/{scheduleId}", event.getId(), scheduleId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());
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

    private TagEntity createTag(String name) {
        return tagJpaRepository.saveAndFlush(
                TagEntity.builder()
                        .tagName(name)
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
