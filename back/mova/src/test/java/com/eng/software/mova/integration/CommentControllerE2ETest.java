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
class CommentControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private CommentJpaRepository commentJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, events, comments CASCADE");
    }

    // ======================== GET /comments/{id} ========================

    @Nested
    @DisplayName("GET /comments/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar comentário por ID retornando 200")
        void shouldFindByIdSuccessfully() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Excelente!");

            mockMvc.perform(get("/comments/{id}", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(comment.getId().toString()))
                    .andExpect(jsonPath("$.content").value("Excelente!"))
                    .andExpect(jsonPath("$.eventId").value(event.getId().toString()))
                    .andExpect(jsonPath("$.userId").value(user.getId().toString()));
        }

        @Test
        @DisplayName("Deve retornar 404 quando comentário não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(get("/comments/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/comments/{id}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /comments/events/{eventId} ========================

    @Nested
    @DisplayName("GET /comments/events/{eventId}")
    class FindByEventIdTests {

        @Test
        @DisplayName("Deve listar comentários de um evento retornando 200")
        void shouldListCommentsForEvent() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            createComment(user, event, "Primeiro!");
            createComment(user, event, "Segundo!");

            mockMvc.perform(get("/comments/events/{eventId}?page=0&size=10", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/comments/events/{eventId}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== POST /comments/events/{eventId} ========================

    @Nested
    @DisplayName("POST /comments/events/{eventId}")
    class CreateCommentTests {

        @Test
        @DisplayName("Deve criar comentário com sucesso retornando 201")
        void shouldCreateCommentSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);

            String jsonPayload = """
                {
                    "content": "Muito bom esse evento!"
                }
                """;

            mockMvc.perform(post("/comments/events/{eventId}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andExpect(jsonPath("$.content").value("Muito bom esse evento!"));

            assertThat(commentJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 400 quando conteúdo for vazio")
        void shouldReturnBadRequestWhenContentIsBlank() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);

            String jsonPayload = """
                {
                    "content": ""
                }
                """;

            mockMvc.perform(post("/comments/events/{eventId}", event.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            String jsonPayload = """
                {
                    "content": "Muito bom esse evento!"
                }
                """;
            mockMvc.perform(post("/comments/events/{eventId}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== POST /comments/{commentId}/likes ========================

    @Nested
    @DisplayName("POST /comments/{commentId}/likes")
    class LikeCommentTests {

        @Test
        @DisplayName("Deve curtir comentário retornando 200")
        void shouldLikeCommentSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Excelente!");

            mockMvc.perform(post("/comments/{commentId}/likes", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve retornar 404 ao curtir comentário inexistente")
        void shouldReturnNotFoundWhenLikingNonExistentComment() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(post("/comments/{commentId}/likes", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(post("/comments/{commentId}/likes", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== PUT /comments/{commentId} ========================

    @Nested
    @DisplayName("PUT /comments/{commentId}")
    class UpdateCommentTests {

        @Test
        @DisplayName("Deve atualizar comentário próprio retornando 200")
        void shouldUpdateCommentSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Ruim");

            String jsonPayload = """
                {
                    "content": "Bom"
                }
                """;

            mockMvc.perform(put("/comments/{commentId}", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value("Bom"));
        }

        @Test
        @DisplayName("Deve retornar 404 se tentar atualizar comentário de outro usuário ou inexistente")
        void shouldReturnNotFoundWhenUpdatingOthersComment() throws Exception {
            UserEntity author = createCommonUser("Author", "author", "author@email.com", "senha");
            UserEntity attacker = createCommonUser("Attacker", "attacker", "attacker@email.com", "senha");
            String attackerToken = getAccessToken(attacker);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(author, venue);
            CommentEntity comment = createComment(author, event, "Meu comentário");

            String jsonPayload = """
                {
                    "content": "Hackeado"
                }
                """;

            mockMvc.perform(put("/comments/{commentId}", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + attackerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            String jsonPayload = """
                {
                    "content": "Bom"
                }
                """;
            mockMvc.perform(put("/comments/{commentId}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== DELETE /comments/{commentId} ========================

    @Nested
    @DisplayName("DELETE /comments/{commentId}")
    class DeleteCommentTests {

        @Test
        @DisplayName("Deve excluir comentário próprio retornando 204")
        void shouldDeleteCommentSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Excluir isso");

            mockMvc.perform(delete("/comments/{commentId}", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(commentJpaRepository.findById(comment.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar 404 ao tentar excluir comentário de outro usuário")
        void shouldReturnNotFoundWhenDeletingOthersComment() throws Exception {
            UserEntity author = createCommonUser("Author", "author", "author@email.com", "senha");
            UserEntity attacker = createCommonUser("Attacker", "attacker", "attacker@email.com", "senha");
            String attackerToken = getAccessToken(attacker);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(author, venue);
            CommentEntity comment = createComment(author, event, "Meu comentário");

            mockMvc.perform(delete("/comments/{commentId}", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + attackerToken))
                    .andExpect(status().isNotFound());
        }
        
        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/comments/{commentId}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== DELETE /comments/{commentId}/likes ========================

    @Nested
    @DisplayName("DELETE /comments/{commentId}/likes")
    class UnlikeCommentTests {

        @Test
        @DisplayName("Deve remover curtida do comentário retornando 200")
        void shouldUnlikeCommentSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Gostei!");
            
            comment.getLikedByUsers().add(user);
            commentJpaRepository.saveAndFlush(comment);

            mockMvc.perform(delete("/comments/{commentId}/likes", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve retornar 404 ao remover curtida de comentário inexistente")
        void shouldReturnNotFoundWhenUnlikingNonExistentComment() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(delete("/comments/{commentId}/likes", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/comments/{commentId}/likes", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== HELPER METHODS ========================

    private VenueEntity createVenue(String name) {
        return venueJpaRepository.saveAndFlush(
                VenueEntity.builder()
                        .name(name)
                        .number("123")
                        .city("Cidade")
                        .street("Rua X")
                        .neighborhood("Bairro Y")
                        .hasParkingLot(false)
                        .hasAccessibility(false)
                        .hasBathroom(false)
                        .hasFoodsAndDrinks(false)
                        .build());
    }

    private EventEntity createEvent(UserEntity creator, VenueEntity venue) {
        return eventJpaRepository.saveAndFlush(
                EventEntity.builder()
                        .user(creator)
                        .venue(venue)
                        .eventName("Show")
                        .description("Descrição do evento")
                        .contentRating("Livre")
                        .price(new BigDecimal("100.00"))
                        .startsAt(LocalDateTime.now().plusDays(1))
                        .endsAt(LocalDateTime.now().plusDays(2))
                        .build()
        );
    }

    private CommentEntity createComment(UserEntity user, EventEntity event, String text) {
        return commentJpaRepository.saveAndFlush(
                CommentEntity.builder()
                        .user(user)
                        .event(event)
                        .comment(text)
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
