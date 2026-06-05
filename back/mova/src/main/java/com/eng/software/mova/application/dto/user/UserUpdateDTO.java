package com.eng.software.mova.application.dto.user;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserUpdateDTO(
        @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,
        @Email(message = "Field email should be valid")
        @Length(max = 255, message = "Email must be at most 255 characters")
        String email,
        @Length(min = 6, max = 20, message = "Password must be between 6 and 255 characters")
        String password
) {
}

