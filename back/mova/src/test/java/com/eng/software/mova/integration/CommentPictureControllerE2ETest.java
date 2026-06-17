package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.FileStoragePort;
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
class CommentPictureControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private CommentJpaRepository commentJpaRepository;
    @Autowired private CommentPictureJpaRepository commentPictureJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private FileStoragePort fileStoragePort;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues, events, comments, comment_pictures CASCADE");
    }

    // ======================== GET /comments/{commentId}/pictures/{id} ========================

    @Nested
    @DisplayName("GET /comments/{commentId}/pictures/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar imagem de comentário por ID retornando 200")
        void shouldFindByIdSuccessfully() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Ótimo evento!");
            CommentPictureEntity picture = createCommentPicture(comment, "https://storage.example.com/pic.jpg");

            mockMvc.perform(get("/comments/{commentId}/pictures/{id}", comment.getId(), picture.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(picture.getId().toString()))
                    .andExpect(jsonPath("$.commentId").value(comment.getId().toString()))
                    .andExpect(jsonPath("$.pictureUrl").value("https://storage.example.com/pic.jpg"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Ótimo evento!");

            mockMvc.perform(get("/comments/{commentId}/pictures/{id}", comment.getId(), UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/comments/{commentId}/pictures/{id}", UUID.randomUUID(), UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /comments/{commentId}/pictures ========================

    @Nested
    @DisplayName("GET /comments/{commentId}/pictures")
    class FindByCommentIdTests {

        @Test
        @DisplayName("Deve listar imagens de um comentário retornando 200")
        void shouldListPicturesForComment() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Ótimo evento!");
            createCommentPicture(comment, "https://storage.example.com/pic1.jpg");
            createCommentPicture(comment, "https://storage.example.com/pic2.jpg");

            mockMvc.perform(get("/comments/{commentId}/pictures?page=0&size=10", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar página vazia se comentário não tiver imagens")
        void shouldReturnEmptyPageWhenNoPictures() throws Exception {
            UserEntity user = createCommonUser("Comprador", "comprador", "comprador@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Sem imagens");

            mockMvc.perform(get("/comments/{commentId}/pictures", comment.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/comments/{commentId}/pictures", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== POST /comments/{commentId}/pictures ========================

    @Nested
    @DisplayName("POST /comments/{commentId}/pictures")
    class UploadPictureTests {

        @Test
        @DisplayName("Deve fazer upload de imagem com sucesso retornando 201")
        void shouldUploadPictureSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Foto do evento!");

            when(fileStoragePort.uploadFile(any())).thenReturn("https://storage.example.com/uploaded.jpg");

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "image.jpg",
                    "image/jpeg",
                    "dummy image content".getBytes()
            );

            mockMvc.perform(multipart("/comments/{commentId}/pictures", comment.getId())
                            .file(file)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.commentId").value(comment.getId().toString()))
                    .andExpect(jsonPath("$.pictureUrl").value("https://storage.example.com/uploaded.jpg"));

            assertThat(commentPictureJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 404 quando comentário não existe")
        void shouldReturnNotFoundWhenCommentDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "image.jpg",
                    "image/jpeg",
                    "dummy image content".getBytes()
            );

            mockMvc.perform(multipart("/comments/{commentId}/pictures", UUID.randomUUID())
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

            mockMvc.perform(multipart("/comments/{commentId}/pictures", UUID.randomUUID())
                            .file(file))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== DELETE /comments/{commentId}/pictures/{id} ========================

    @Nested
    @DisplayName("DELETE /comments/{commentId}/pictures/{id}")
    class DeletePictureTests {

        @Test
        @DisplayName("Deve excluir imagem com sucesso retornando 204")
        void shouldDeletePictureSuccessfully() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Minha foto!");
            CommentPictureEntity picture = createCommentPicture(comment, "https://storage.example.com/pic.jpg");

            mockMvc.perform(delete("/comments/{commentId}/pictures/{id}", comment.getId(), picture.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(commentPictureJpaRepository.findById(picture.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar 404 quando imagem não existe")
        void shouldReturnNotFoundWhenPictureDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);
            VenueEntity venue = createVenue("Local");
            EventEntity event = createEvent(user, venue);
            CommentEntity comment = createComment(user, event, "Opa!");

            mockMvc.perform(delete("/comments/{commentId}/pictures/{id}", comment.getId(), UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/comments/{commentId}/pictures/{id}", UUID.randomUUID(), UUID.randomUUID()))
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

    private CommentPictureEntity createCommentPicture(CommentEntity comment, String url) {
        return commentPictureJpaRepository.saveAndFlush(
                CommentPictureEntity.builder()
                        .comment(comment)
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
