package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.TagEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.repository.TagJpaRepository;
import com.eng.software.mova.infrastructure.persistence.repository.UserJpaRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class TagControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private TagJpaRepository tagJpaRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, tags CASCADE");
    }

    // ======================== GET /tags ========================

    @Nested
    @DisplayName("GET /tags")
    class FindAllTests {

        @Test
        @DisplayName("Deve listar todas as tags retornando 200")
        void shouldFindAllTags() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            createTag("Música Ao Vivo");
            createTag("Teatro");

            mockMvc.perform(get("/tags")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/tags"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /tags/{id} ========================

    @Nested
    @DisplayName("GET /tags/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar tag por ID retornando 200")
        void shouldFindTagById() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            TagEntity tag = createTag("Cinema");

            mockMvc.perform(get("/tags/{id}", tag.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(tag.getId().toString()))
                    .andExpect(jsonPath("$.tagName").value("Cinema"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("User", "user", "user@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(get("/tags/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== POST /tags ========================

    @Nested
    @DisplayName("POST /tags")
    class CreateTagTests {

        @Test
        @DisplayName("Deve criar tag com sucesso como ADMIN retornando 201")
        void shouldCreateTagSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "tagName": "Esportes"
                }
                """;

            mockMvc.perform(post("/tags")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.tagName").value("Esportes"));

            assertThat(tagJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            String jsonPayload = """
                {
                    "tagName": "Dança"
                }
                """;

            mockMvc.perform(post("/tags")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome está em branco")
        void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "tagName": ""
                }
                """;

            mockMvc.perform(post("/tags")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 409 quando nome já existe")
        void shouldReturnConflictWhenNameAlreadyExists() throws Exception {
            createTag("Esportes");

            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "tagName": "Esportes"
                }
                """;

            mockMvc.perform(post("/tags")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isConflict());
        }
    }

    // ======================== PUT /tags/{id} ========================

    @Nested
    @DisplayName("PUT /tags/{id}")
    class UpdateTagTests {

        @Test
        @DisplayName("Deve atualizar tag com sucesso como ADMIN retornando 200")
        void shouldUpdateTagSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            TagEntity tag = createTag("Exposição");

            String jsonPayload = """
                {
                    "tagName": "Exposição de Arte"
                }
                """;

            mockMvc.perform(put("/tags/{id}", tag.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tagName").value("Exposição de Arte"));

            TagEntity updated = tagJpaRepository.findById(tag.getId()).orElseThrow();
            assertThat(updated.getTagName()).isEqualTo("Exposição de Arte");
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            mockMvc.perform(put("/tags/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    { "tagName": "Nova Tag" }
                                    """))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            mockMvc.perform(put("/tags/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    { "tagName": "Nova Tag" }
                                    """))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 409 quando nome já existe")
        void shouldReturnConflictWhenNameAlreadyExists() throws Exception {
            TagEntity tagToUpdate = createTag("Exposição");
            createTag("Museu");

            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "tagName": "Museu"
                }
                """;

            mockMvc.perform(put("/tags/{id}", tagToUpdate.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isConflict());
        }
    }

    // ======================== DELETE /tags/{id} ========================

    @Nested
    @DisplayName("DELETE /tags/{id}")
    class DeleteTagTests {

        @Test
        @DisplayName("Deve excluir tag com sucesso como ADMIN retornando 204")
        void shouldDeleteTagSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            TagEntity tag = createTag("Esportes");

            mockMvc.perform(delete("/tags/{id}", tag.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(tagJpaRepository.findById(tag.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "senha");
            String token = getAccessToken(common);

            mockMvc.perform(delete("/tags/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "senha");
            String token = getAccessToken(admin);

            mockMvc.perform(delete("/tags/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== HELPER METHODS ========================

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
