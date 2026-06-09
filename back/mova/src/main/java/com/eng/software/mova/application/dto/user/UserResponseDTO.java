package com.eng.software.mova.application.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        String name,
        String username,
        String email,
        String avatarUrl,
        String bio,
        String location,
        boolean isPrivate,
        String userType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isActive) {
}

