package com.eng.software.mova.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para criação de um novo usuário")
public record UserCreateDTO(
        @NotBlank(message = "Name cannot be blank")
        @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        @Schema(description = "Nome completo do usuário", example = "João da Silva", minLength = 3, maxLength = 255)
        String name,

        @NotBlank(message = "Username cannot be blank")
        @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Nome de usuário único (usado no perfil público)", example = "joaosilva", minLength = 3, maxLength = 50)
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Field email should be valid")
        @Length(max = 255, message = "Email must be at most 255 characters")
        @Schema(description = "Endereço de e-mail válido e único", example = "joao@email.com", maxLength = 255)
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        @Schema(description = "Senha do usuário", example = "Senha123", minLength = 6, maxLength = 20)
        String password
) {}
