package com.eng.software.mova.integration;

import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.infrastructure.persistence.repository.VenueJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import org.springframework.http.HttpHeaders;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class VenueControllerE2ETest {

    @Autowired private MockMvc mockMvc;
    @Autowired private VenueJpaRepository venueJpaRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Auth0JwtTokenProvider jwtTokenProvider;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, venues CASCADE");
    }

    // ======================== GET /venues ========================

    @Nested
    @DisplayName("GET /venues")
    class ListVenuesTests {

        @Test
        @DisplayName("Deve listar locais paginados com sucesso")
        void shouldListVenuesWithPagination() throws Exception {
            UserEntity user = createCommonUser("João", "joao", "joao@email.com", "senha");
            String token = getAccessToken(user);

            createVenue("Parque Ibirapuera", "S/N", "São Paulo", "Av. Pedro Álvares Cabral", "Vila Mariana");
            createVenue("MASP", "1578", "São Paulo", "Av. Paulista", "Bela Vista");

            mockMvc.perform(get("/venues?page=0&size=10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/venues"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /venues/{id} ========================

    @Nested
    @DisplayName("GET /venues/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve buscar local por ID retornando 200")
        void shouldFindVenueById() throws Exception {
            UserEntity user = createCommonUser("João", "joao", "joao@email.com", "senha");
            String token = getAccessToken(user);

            VenueEntity venue = createVenue("Parque Ibirapuera", "S/N", "São Paulo", "Av. Pedro Álvares Cabral", "Vila Mariana");

            mockMvc.perform(get("/venues/{id}", venue.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(venue.getId().toString()))
                    .andExpect(jsonPath("$.name").value("Parque Ibirapuera"))
                    .andExpect(jsonPath("$.city").value("São Paulo"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity user = createCommonUser("João", "joao", "joao@email.com", "senha");
            String token = getAccessToken(user);

            mockMvc.perform(get("/venues/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/venues/{id}", UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== GET /venues/search ========================

    @Nested
    @DisplayName("GET /venues/search")
    class SearchVenuesTests {

        @Test
        @DisplayName("Deve buscar locais com filtros opcionais retornando 200")
        void shouldSearchVenues() throws Exception {
            UserEntity user = createCommonUser("João", "joao", "joao@email.com", "senha");
            String token = getAccessToken(user);

            createVenue("Parque Ibirapuera", "S/N", "São Paulo", "Av. Pedro Álvares Cabral", "Vila Mariana");
            createVenue("Parque Villa-Lobos", "2001", "São Paulo", "Av. Prof. Fonseca Rodrigues", "Alto de Pinheiros");
            createVenue("Praça da Liberdade", "S/N", "Belo Horizonte", "Praça da Liberdade", "Savassi");

            // Buscar por "Parque" e "São Paulo"
            mockMvc.perform(get("/venues/search")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .param("name", "Parque")
                            .param("city", "São Paulo")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2));
        }

        @Test
        @DisplayName("Deve retornar 401 sem autenticação")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            mockMvc.perform(get("/venues/search"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================== POST /venues (Admin) ========================

    @Nested
    @DisplayName("POST /venues")
    class CreateVenueTests {

        @Test
        @DisplayName("Deve criar local com sucesso como ADMIN retornando 201")
        void shouldCreateVenueSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "name": "Parque Ibirapuera",
                    "number": "S/N",
                    "city": "São Paulo",
                    "street": "Av. Pedro Álvares Cabral",
                    "neighborhood": "Vila Mariana",
                    "landmark": "Próximo ao Obelisco",
                    "hasParkingLot": true,
                    "hasAccessibility": true,
                    "hasBathroom": true,
                    "hasFoodsAndDrinks": true
                }
                """;

            mockMvc.perform(post("/venues")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value("Parque Ibirapuera"))
                    .andExpect(jsonPath("$.city").value("São Paulo"))
                    .andExpect(jsonPath("$.hasParkingLot").value(true));

            assertThat(venueJpaRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            String jsonPayload = """
                {
                    "name": "Parque Ibirapuera",
                    "number": "S/N",
                    "city": "São Paulo",
                    "street": "Av. Pedro Álvares Cabral",
                    "neighborhood": "Vila Mariana",
                    "hasParkingLot": false,
                    "hasAccessibility": true,
                    "hasBathroom": true,
                    "hasFoodsAndDrinks": false
                }
                """;

            mockMvc.perform(post("/venues")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve retornar 400 quando campos obrigatórios estão ausentes")
        void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            String jsonPayload = """
                {
                    "name": "",
                    "city": "São Paulo"
                }
                """;

            mockMvc.perform(post("/venues")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isBadRequest());
        }
    }

    // ======================== PUT /venues/{id} (Admin) ========================

    @Nested
    @DisplayName("PUT /venues/{id}")
    class UpdateVenueTests {

        @Test
        @DisplayName("Deve atualizar local com sucesso como ADMIN retornando 200")
        void shouldUpdateVenueSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Parque", "S/N", "São Paulo", "Av.", "Bairro");

            String jsonPayload = """
                {
                    "name": "Parque Ibirapuera Atualizado",
                    "city": "São Paulo (SP)",
                    "hasParkingLot": true
                }
                """;

            mockMvc.perform(put("/venues/{id}", venue.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Parque Ibirapuera Atualizado"))
                    .andExpect(jsonPath("$.city").value("São Paulo (SP)"))
                    .andExpect(jsonPath("$.hasParkingLot").value(true));

            VenueEntity updated = venueJpaRepository.findById(venue.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("Parque Ibirapuera Atualizado");
            assertThat(updated.getCity()).isEqualTo("São Paulo (SP)");
            assertThat(updated.isHasParkingLot()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            String validPayload = """
                {
                    "name": "Local",
                    "number": "S/N",
                    "city": "São Paulo",
                    "street": "Rua X",
                    "neighborhood": "Bairro"
                }
                """;

            mockMvc.perform(put("/venues/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validPayload))
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            String validPayload = """
                {
                    "name": "Local",
                    "number": "S/N",
                    "city": "São Paulo",
                    "street": "Rua X",
                    "neighborhood": "Bairro"
                }
                """;

            mockMvc.perform(put("/venues/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validPayload))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== DELETE /venues/{id} (Admin) ========================

    @Nested
    @DisplayName("DELETE /venues/{id}")
    class DeleteVenueTests {

        @Test
        @DisplayName("Deve excluir local com sucesso como ADMIN retornando 204")
        void shouldDeleteVenueSuccessfully() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            VenueEntity venue = createVenue("Parque", "S/N", "São Paulo", "Av.", "Bairro");

            mockMvc.perform(delete("/venues/{id}", venue.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(venueJpaRepository.findById(venue.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar 403 como usuário COMMON")
        void shouldReturnForbiddenForCommonUser() throws Exception {
            UserEntity common = createCommonUser("Common", "common", "common@email.com", "Senha123");
            String token = getAccessToken(common);

            mockMvc.perform(delete("/venues/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @DisplayName("Deve retornar 404 quando ID não existe")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            UserEntity admin = createAdminUser("Admin", "admin", "admin@email.com", "Senha123");
            String token = getAccessToken(admin);

            mockMvc.perform(delete("/venues/{id}", UUID.randomUUID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== HELPER METHODS ========================

    private VenueEntity createVenue(String name, String number, String city, String street, String neighborhood) {
        return venueJpaRepository.saveAndFlush(
                VenueEntity.builder()
                        .name(name)
                        .number(number)
                        .city(city)
                        .street(street)
                        .neighborhood(neighborhood)
                        .hasParkingLot(false)
                        .hasAccessibility(false)
                        .hasBathroom(false)
                        .hasFoodsAndDrinks(false)
                        .build());
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
