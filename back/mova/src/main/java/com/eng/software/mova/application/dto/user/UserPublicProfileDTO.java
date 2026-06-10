package com.eng.software.mova.application.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserPublicProfileDTO(
        UUID id,
        String name,
        String username,
        String avatarUrl,
        String bio,
        String location,
        LocalDateTime createdAt) {
}
