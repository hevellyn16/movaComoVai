package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.auth.JwtResponse;
import com.eng.software.mova.application.dto.auth.LoginRequest;
import com.eng.software.mova.application.dto.auth.PasswordRequestDTO;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final Auth0JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        assert userDetails != null;
        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId().toString())
                .username(userDetails.getUsername())
                .role(userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority())
                .email(userDetails.getEmail())
                .build());
    }
}