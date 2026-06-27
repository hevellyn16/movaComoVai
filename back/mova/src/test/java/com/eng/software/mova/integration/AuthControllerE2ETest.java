package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class AuthControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    // ======================== POST /auth/login ========================

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("Deve fazer login com sucesso e retornar JWT")
        void shouldLoginSuccessfully() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "username": "joao@email.com",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.email").value("joao@email.com"))
                    .andExpect(jsonPath("$.role").value("ROLE_COMMON"));
        }

        @Test
        @DisplayName("Deve fazer login com username com sucesso e retornar JWT")
        void shouldLoginWithUsernameSuccessfully() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "username": "joaosilva",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.username").value("joaosilva"))
                    .andExpect(jsonPath("$.role").value("ROLE_COMMON"));
        }

        @Test
        @DisplayName("Deve retornar 401 quando credenciais são inválidas")
        void shouldReturnUnauthorizedWhenBadCredentials() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "username": "joao@email.com",
                    "password": "SenhaErrada"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 quando usuário não existe")
        void shouldReturnUnauthorizedWhenUserDoesNotExist() throws Exception {
            String jsonPayload = """
                {
                    "username": "naoexiste@email.com",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 400 quando campos são omitidos")
        void shouldReturnBadRequestWhenFieldsAreMissing() throws Exception {
            String jsonPayload = """
                {
                    "username": "joao@email.com"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================== HELPER METHODS ========================

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
}
