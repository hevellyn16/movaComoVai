package com.eng.software.mova.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para atualização do perfil do usuário. Todos os campos são opcionais — envie apenas os que deseja alterar.")
public record UserUpdateDTO(
        @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        @Schema(description = "Nome completo do usuário", example = "João da Silva Atualizado", minLength = 3, maxLength = 255)
        String name,

        @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Nome de usuário único", example = "joaosilva_novo", minLength = 3, maxLength = 50)
        String username,

        @Email(message = "Field email should be valid")
        @Length(max = 255, message = "Email must be at most 255 characters")
        @Schema(description = "Novo endereço de e-mail", example = "joao.novo@email.com", maxLength = 255)
        String email,

        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        @Schema(description = "Nova senha", example = "NovaSenha123", minLength = 6, maxLength = 20)
        String password,

        @Length(max = 500, message = "Avatar URL must be at most 500 characters")
        @Schema(description = "URL da foto de perfil", example = "http://localhost:8080/uploads/avatars/avatar.png", maxLength = 500)
        String avatarUrl,

        @Schema(description = "Biografia/descrição do usuário", example = "Amante de eventos culturais e música ao vivo 🎵")
        String bio,

        @Length(max = 100, message = "Location must be at most 100 characters")
        @Schema(description = "Localização do usuário", example = "São Paulo, SP", maxLength = 100)
        String location,

        @Schema(description = "Define se o perfil é privado", example = "false")
        Boolean isPrivate,

        @Schema(description = "Habilita notificações push", example = "true")
        Boolean pushNotifications,

        @Schema(description = "Habilita notificações por e-mail", example = "true")
        Boolean emailNotifications
) {}
