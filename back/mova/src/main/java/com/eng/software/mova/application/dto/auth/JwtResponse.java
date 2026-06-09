package com.eng.software.mova.application.dto.auth;

import lombok.Builder;

@Builder
public record JwtResponse (
        String token,
        String id,
        String username,
        String role,
        String email
) {
}
