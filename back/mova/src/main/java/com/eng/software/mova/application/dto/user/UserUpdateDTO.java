package com.eng.software.mova.application.dto.user;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserUpdateDTO(
        @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email(message = "Field email should be valid")
        @Length(max = 255, message = "Email must be at most 255 characters")
        String email,

        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        @Length(max = 500, message = "Avatar URL must be at most 500 characters")
        String avatarUrl,

        String bio,

        @Length(max = 100, message = "Location must be at most 100 characters")
        String location,

        Boolean isPrivate,
        Boolean pushNotifications,
        Boolean emailNotifications
) {}

