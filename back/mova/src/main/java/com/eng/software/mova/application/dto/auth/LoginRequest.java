package com.eng.software.mova.application.dto.auth;

public record LoginRequest(
        String username,
        String password
) {
}
