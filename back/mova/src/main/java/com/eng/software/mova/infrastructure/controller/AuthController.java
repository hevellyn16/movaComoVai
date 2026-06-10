package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.auth.JwtResponse;
import com.eng.software.mova.application.dto.auth.LoginRequest;
import com.eng.software.mova.application.dto.auth.PasswordRequestDTO;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints de autenticação: login com e-mail/senha e geração de token JWT.")
public class AuthController {
    private final AuthenticationManager authManager;
    private final Auth0JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "Realizar login",
            description = "Autentica o usuário com e-mail/username e senha, retornando um token JWT válido para uso nas demais rotas protegidas. "
                    + "O token deve ser enviado no header `Authorization: Bearer {token}`. "
                    + "**Acesso: público (sem autenticação).**",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso — token JWT retornado",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (campos obrigatórios ausentes)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas — e-mail/username ou senha incorretos", content = @Content)
    })
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