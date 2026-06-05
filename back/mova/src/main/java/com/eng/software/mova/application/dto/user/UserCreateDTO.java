package com.eng.software.mova.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserCreateDTO(
        @NotBlank(message = "Name cannot be blank")
        @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Field email should be valid")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password) {
}
