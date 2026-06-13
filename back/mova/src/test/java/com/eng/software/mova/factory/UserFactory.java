package com.eng.software.mova.factory;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserPublicProfileDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Factory para criação de objetos de teste relacionados a User.
 * Centraliza a construção de entidades de domínio e DTOs,
 * garantindo dados consistentes e reutilizáveis em toda a suíte de testes.
 */
public final class UserFactory {

    public static final UUID DEFAULT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final String DEFAULT_NAME = "João da Silva";
    public static final String DEFAULT_USERNAME = "joaosilva";
    public static final String DEFAULT_EMAIL = "joao@email.com";
    public static final String DEFAULT_PASSWORD = "Senha123";
    public static final String DEFAULT_ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";
    public static final String DEFAULT_BIO = "Amante de eventos culturais";
    public static final String DEFAULT_LOCATION = "São Paulo, SP";
    public static final String DEFAULT_AVATAR_URL = "http://localhost:8080/uploads/avatars/avatar.png";
    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.of(2025, 1, 15, 10, 30, 0);
    public static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.of(2025, 6, 10, 14, 0, 0);

    private UserFactory() {
        // Utility class — impede instanciação
    }

    // ======================== DOMAIN MODEL ========================

    /**
     * Cria um User de domínio com todos os campos preenchidos (COMMON, ativo, público).
     */
    public static User createDefaultUser() {
        return User.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .password(DEFAULT_ENCODED_PASSWORD)
                .userType(UserType.COMMON)
                .createdAt(DEFAULT_CREATED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .isActive(true)
                .avatarUrl(DEFAULT_AVATAR_URL)
                .bio(DEFAULT_BIO)
                .location(DEFAULT_LOCATION)
                .isPrivate(false)
                .pushNotifications(true)
                .emailNotifications(true)
                .tagsId(new HashSet<>())
                .build();
    }

    /**
     * Cria um User de domínio com ID e email customizados.
     */
    public static User createUserWithIdAndEmail(UUID id, String email) {
        return User.builder()
                .id(id)
                .name(DEFAULT_NAME)
                .username(email.split("@")[0])
                .email(email)
                .password(DEFAULT_ENCODED_PASSWORD)
                .userType(UserType.COMMON)
                .createdAt(DEFAULT_CREATED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .isActive(true)
                .isPrivate(false)
                .pushNotifications(true)
                .emailNotifications(true)
                .tagsId(new HashSet<>())
                .build();
    }

    /**
     * Cria um User ADMIN.
     */
    public static User createAdminUser() {
        User user = createDefaultUser();
        user.setUserType(UserType.ADMIN);
        return user;
    }

    /**
     * Cria um User com perfil privado.
     */
    public static User createPrivateUser() {
        User user = createDefaultUser();
        user.setPrivate(true);
        return user;
    }

    /**
     * Cria um User com tags associadas.
     */
    public static User createUserWithTags(Set<UUID> tagIds) {
        User user = createDefaultUser();
        user.setTagsId(new HashSet<>(tagIds));
        return user;
    }

    /**
     * Cria um User inativo (soft-deleted).
     */
    public static User createInactiveUser() {
        User user = createDefaultUser();
        user.setActive(false);
        return user;
    }

    // ======================== CREATE DTO ========================

    /**
     * Cria um UserCreateDTO padrão válido.
     */
    public static UserCreateDTO createDefaultUserCreateDTO() {
        return new UserCreateDTO(
                DEFAULT_NAME,
                DEFAULT_USERNAME,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD
        );
    }

    /**
     * Cria um UserCreateDTO com email e username customizados.
     */
    public static UserCreateDTO createUserCreateDTO(String name, String username, String email, String password) {
        return new UserCreateDTO(name, username, email, password);
    }

    // ======================== UPDATE DTO ========================

    /**
     * Cria um UserUpdateDTO com todos os campos preenchidos.
     */
    public static UserUpdateDTO createFullUpdateDTO() {
        return new UserUpdateDTO(
                "João Atualizado",
                "joao_novo",
                "joao.novo@email.com",
                "NovaSenha123",
                "http://localhost:8080/uploads/avatars/new_avatar.png",
                "Bio atualizada",
                "Rio de Janeiro, RJ",
                true,
                false,
                false
        );
    }

    /**
     * Cria um UserUpdateDTO parcial — apenas nome.
     */
    public static UserUpdateDTO createNameOnlyUpdateDTO(String name) {
        return new UserUpdateDTO(name, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Cria um UserUpdateDTO parcial — apenas email.
     */
    public static UserUpdateDTO createEmailOnlyUpdateDTO(String email) {
        return new UserUpdateDTO(null, null, email, null, null, null, null, null, null, null);
    }

    /**
     * Cria um UserUpdateDTO parcial — apenas username.
     */
    public static UserUpdateDTO createUsernameOnlyUpdateDTO(String username) {
        return new UserUpdateDTO(null, username, null, null, null, null, null, null, null, null);
    }

    /**
     * Cria um UserUpdateDTO vazio (nenhum campo alterado).
     */
    public static UserUpdateDTO createEmptyUpdateDTO() {
        return new UserUpdateDTO(null, null, null, null, null, null, null, null, null, null);
    }

    // ======================== RESPONSE DTO ========================

    /**
     * Cria um UserResponseDTO correspondente ao User padrão.
     */
    public static UserResponseDTO createDefaultUserResponseDTO() {
        return UserResponseDTO.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .username(DEFAULT_USERNAME)
                .email(DEFAULT_EMAIL)
                .avatarUrl(DEFAULT_AVATAR_URL)
                .bio(DEFAULT_BIO)
                .location(DEFAULT_LOCATION)
                .isPrivate(false)
                .userType(UserType.COMMON.name())
                .createdAt(DEFAULT_CREATED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .isActive(true)
                .build();
    }

    // ======================== PUBLIC PROFILE DTO ========================

    /**
     * Cria um UserPublicProfileDTO correspondente ao User padrão.
     */
    public static UserPublicProfileDTO createDefaultPublicProfileDTO() {
        return UserPublicProfileDTO.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .username(DEFAULT_USERNAME)
                .avatarUrl(DEFAULT_AVATAR_URL)
                .bio(DEFAULT_BIO)
                .location(DEFAULT_LOCATION)
                .createdAt(DEFAULT_CREATED_AT)
                .build();
    }
}
