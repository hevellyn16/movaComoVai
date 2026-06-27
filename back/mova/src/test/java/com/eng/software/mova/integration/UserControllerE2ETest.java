package com.eng.software.mova.integration;

import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.EmailGateway;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.repository.UserJpaRepository;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class UserControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;

    @MockitoBean private EmailGateway emailGateway;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    // ======================== POST /users ========================

    @Nested
    @DisplayName("POST /users")
    class CreateUserTests {

        @Test
        @DisplayName("Deve criar usuário com sucesso retornando 201")
        void shouldCreateUserSuccessfully() throws Exception {
            String jsonPayload = """
                {
                    "name": "João Silva",
                    "username": "joaosilva",
                    "email": "joao@email.com",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andExpect(jsonPath("$.username").value("joaosilva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"))
                    .andExpect(jsonPath("$.userType").value("COMMON"))
                    .andExpect(jsonPath("$.isActive").value(true));

            assertThat(userJpaRepository.existsByEmail("joao@email.com")).isTrue();
            assertThat(userJpaRepository.existsByUsername("joaosilva")).isTrue();
        }

        @Test
        @DisplayName("Deve retornar 409 quando email já existe")
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "name": "Outro Nome",
                    "username": "outrousername",
                    "email": "joao@email.com",
                    "password": "Senha456"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Deve retornar 409 quando username já existe")
        void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "name": "Outro Nome",
                    "username": "joaosilva",
                    "email": "outro@email.com",
                    "password": "Senha456"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Deve retornar 400 quando nome está em branco")
        void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
            String jsonPayload = """
                {
                    "name": "",
                    "username": "joaosilva",
                    "email": "joao@email.com",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando email é inválido")
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            String jsonPayload = """
                {
                    "name": "João Silva",
                    "username": "joaosilva",
                    "email": "email-invalido",
                    "password": "Senha123"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando senha é muito curta")
        void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
            String jsonPayload = """
                {
                    "name": "João Silva",
                    "username": "joaosilva",
                    "email": "joao@email.com",
                    "password": "123"
                }
                """;

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }
    }


    // ======================== GET /users/profile/{username} ========================

    @Nested
    @DisplayName("GET /users/profile/{username}")
    class PublicProfileTests {

        @Test
        @DisplayName("Deve retornar perfil público com sucesso")
        void shouldReturnPublicProfile() throws Exception {
            createCommonUser("Maria Santos", "mariasantos", "maria@email.com", "Senha123");

            mockMvc.perform(get("/users/profile/{username}", "mariasantos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Maria Santos"))
                    .andExpect(jsonPath("$.username").value("mariasantos"));
        }

        @Test
        @DisplayName("Deve retornar 403 quando perfil é privado")
        void shouldReturnForbiddenWhenProfileIsPrivate() throws Exception {
            createPrivateUser("Privado User", "userprivado", "privado@email.com", "Senha123");

            mockMvc.perform(get("/users/profile/{username}", "userprivado"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 404 quando username não existe")
        void shouldReturnNotFoundWhenUsernameDoesNotExist() throws Exception {
            mockMvc.perform(get("/users/profile/{username}", "naoexiste"))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== PUT /users ========================

    @Nested
    @DisplayName("PUT /users")
    class UpdateUserTests {

        @Test
        @DisplayName("Deve atualizar usuário com sucesso retornando 200")
        void shouldUpdateUserSuccessfully() throws Exception {
            UserEntity user = createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");
            String token = getAccessToken(user);

            String jsonPayload = """
                {
                    "name": "João Atualizado",
                    "bio": "Minha bio",
                    "location": "São Paulo, SP"
                }
                """;

            mockMvc.perform(put("/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload)
                            .requestAttr("userId", user.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("João Atualizado"))
                    .andExpect(jsonPath("$.bio").value("Minha bio"))
                    .andExpect(jsonPath("$.location").value("São Paulo, SP"));

            UserEntity updated = userJpaRepository.findById(user.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("João Atualizado");
            assertThat(updated.getBio()).isEqualTo("Minha bio");
        }

        @Test
        @DisplayName("Deve retornar 409 ao atualizar com email já existente")
        void shouldReturnConflictWhenUpdatingToExistingEmail() throws Exception {
            createCommonUser("User Um", "userum", "um@email.com", "Senha123");
            UserEntity userTwo = createCommonUser("User Dois", "userdois", "dois@email.com", "Senha123");
            String token = getAccessToken(userTwo);

            String jsonPayload = """
                {
                    "email": "um@email.com"
                }
                """;

            mockMvc.perform(put("/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload)
                            .requestAttr("userId", userTwo.getId().toString()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Deve retornar 401 sem token de autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(put("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== DELETE /users ========================

    @Nested
    @DisplayName("DELETE /users")
    class DeleteUserTests {

        @Test
        @DisplayName("Deve excluir conta com sucesso retornando 204")
        void shouldDeleteUserSuccessfully() throws Exception {
            UserEntity user = createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");
            String token = getAccessToken(user);

            mockMvc.perform(delete("/users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .requestAttr("userId", user.getId().toString()))
                    .andExpect(status().isNoContent());

            Boolean isDeleted = jdbcTemplate.queryForObject(
                    "SELECT NOT is_active FROM users WHERE id = ?",
                    Boolean.class, user.getId());
            assertThat(isDeleted).isTrue();
        }

        @Test
        @DisplayName("Deve retornar 401 sem token de autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(delete("/users"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /users/{id} (Admin) ========================

    @Nested
    @DisplayName("GET /users/{id}")
    class FindByIdAdminTests {

        @Test
        @DisplayName("Deve buscar usuário por ID como ADMIN retornando 200")
        void shouldFindUserByIdAsAdmin() throws Exception {
            UserEntity user = createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(get("/users/{id}", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId().toString()))
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao@email.com"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(get("/users/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            mockMvc.perform(get("/users/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/users/{id}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /users/email/{email} (Admin) ========================

    @Nested
    @DisplayName("GET /users/email/{email}")
    class FindByEmailAdminTests {

        @Test
        @DisplayName("Deve buscar usuário por email como ADMIN retornando 200")
        void shouldFindUserByEmailAsAdmin() throws Exception {
            createCommonUser("Maria Santos", "mariasantos", "maria@email.com", "Senha123");
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(get("/users/email/{email}", "maria@email.com")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("maria@email.com"))
                    .andExpect(jsonPath("$.name").value("Maria Santos"));
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            mockMvc.perform(get("/users/email/{email}", "qualquer@email.com")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== GET /users (Admin) ========================

    @Nested
    @DisplayName("GET /users")
    class ListUsersAdminTests {

        @Test
        @DisplayName("Deve listar usuários paginados como ADMIN")
        void shouldListUsersWithPagination() throws Exception {
            createCommonUser("User One", "userone", "one@email.com", "Senha123");
            createCommonUser("User Two", "usertwo", "two@email.com", "Senha123");
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(get("/users?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(3));
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            mockMvc.perform(get("/users?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== PUT /users/{id}/admin (Admin) ========================

    @Nested
    @DisplayName("PUT /users/{id}/admin")
    class PromoteToAdminTests {

        @Test
        @DisplayName("Deve promover usuário para ADMIN com sucesso")
        void shouldPromoteUserToAdmin() throws Exception {
            UserEntity user = createCommonUser("User Common", "usercommon", "common@email.com", "Senha123");
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(put("/users/{id}/admin", user.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userType").value("ADMIN"));

            UserEntity updated = userJpaRepository.findById(user.getId()).orElseThrow();
            assertThat(updated.getUserType()).isEqualTo(UserType.ADMIN); // Assumindo que você tem um Enum UserType.ADMIN
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            mockMvc.perform(put("/users/{id}/admin", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================== POST /users/forgot-password ========================

    @Nested
    @DisplayName("POST /users/forgot-password")
    class ForgotPasswordTests {

        @Test
        @DisplayName("Deve enviar email de recuperação retornando 204")
        void shouldSendForgotPasswordEmail() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String jsonPayload = """
                {
                    "email": "joao@email.com"
                }
                """;

            mockMvc.perform(post("/users/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNoContent());

            verify(emailGateway).sendHtmlEmail(
                    eq("joao@email.com"),
                    eq("Recuperacao de senha"),
                    eq("reset-password"),
                    anyMap());
        }

        @Test
        @DisplayName("Deve retornar 404 quando email não está cadastrado")
        void shouldReturnNotFoundWhenEmailDoesNotExist() throws Exception {
            String jsonPayload = """
                {
                    "email": "naoexiste@email.com"
                }
                """;

            mockMvc.perform(post("/users/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== PUT /users/reset-password ========================

    @Nested
    @DisplayName("PUT /users/reset-password")
    class ResetPasswordTests {

        @Test
        @DisplayName("Deve redefinir senha com sucesso e conseguir logar com a nova senha")
        void shouldResetPasswordSuccessfully() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String resetToken = jwtTokenProvider.generatePasswordResetToken("joao@email.com");

            String jsonPayload = """
                {
                    "password": "NovaSenha123",
                    "confirmPassword": "NovaSenha123"
                }
                """;

            mockMvc.perform(put("/users/reset-password")
                            .param("token", resetToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isNoContent());

            // Verificar login com nova senha
            String loginPayload = """
                {
                    "username": "joao@email.com",
                    "password": "NovaSenha123"
                }
                """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Deve retornar 400 quando senhas não coincidem")
        void shouldReturnBadRequestWhenPasswordsDoNotMatch() throws Exception {
            createCommonUser("João Silva", "joaosilva", "joao@email.com", "Senha123");

            String resetToken = jwtTokenProvider.generatePasswordResetToken("joao@email.com");

            String jsonPayload = """
                {
                    "password": "NovaSenha123",
                    "confirmPassword": "SenhaDiferente"
                }
                """;

            mockMvc.perform(put("/users/reset-password")
                            .param("token", resetToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar erro quando token de reset é inválido")
        void shouldReturnErrorWhenResetTokenIsInvalid() throws Exception {
            String jsonPayload = """
                {
                    "password": "NovaSenha123",
                    "confirmPassword": "NovaSenha123"
                }
                """;

            mockMvc.perform(put("/users/reset-password")
                            .param("token", "token-invalido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().is4xxClientError());
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
                        .userType(UserType.COMMON) // Assumindo Enum UserType
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

    private UserEntity createPrivateUser(String name, String username, String email, String rawPassword) {
        return userJpaRepository.saveAndFlush(
                UserEntity.builder()
                        .name(name)
                        .username(username)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .userType(UserType.COMMON) // Assumindo Enum UserType
                        .createdAt(LocalDateTime.now())
                        .isActive(true)
                        .isPrivate(true)
                        .pushNotifications(true)
                        .emailNotifications(true)
                        .build());
    }
}
