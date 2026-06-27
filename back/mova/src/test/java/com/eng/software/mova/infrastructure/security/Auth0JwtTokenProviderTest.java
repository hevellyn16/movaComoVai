package com.eng.software.mova.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.eng.software.mova.shared.exceptions.InvalidAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auth0JwtTokenProvider — Testes Unitários")
class Auth0JwtTokenProviderTest {

    private Auth0JwtTokenProvider tokenProvider;

    private static final String SECRET = "mySuperSecretKeyForTesting";
    private static final long EXPIRATION_MS = 3600000L; // 1 hora
    private static final long RESET_EXPIRATION_MS = 900000L; // 15 minutos
    private static final String ISSUER = "BarbeShopAPI"; // Hardcoded na classe original

    @BeforeEach
    void setUp() {
        tokenProvider = new Auth0JwtTokenProvider();
        // Injetando os valores das @Value
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", EXPIRATION_MS);
        ReflectionTestUtils.setField(tokenProvider, "jwtResetExpirationInMs", RESET_EXPIRATION_MS);

        // Chamando o @PostConstruct manualmente
        tokenProvider.init();
    }

    private CustomUserDetails createMockUserDetails() {
        return CustomUserDetails.builder()
                .id(UUID.randomUUID())
                .username("João Silva")
                .email("joao@email.com")
                .password("senha123")
                .authorities("COMMON") // O getter transforma em ROLE_COMMON
                .build();
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("Deve gerar JWT válido com Authentication (CustomUserDetails)")
        void shouldGenerateTokenFromAuthentication() {
            // given
            CustomUserDetails userDetails = createMockUserDetails();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // when
            String token = tokenProvider.generateToken(authentication);

            // then
            assertThat(token).isNotBlank();

            // Verificar claims manualmente
            var decoded = JWT.require(Algorithm.HMAC512(SECRET))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);

            assertThat(decoded.getSubject()).isEqualTo("joao@email.com");
            assertThat(decoded.getClaim("id").asString()).isEqualTo(userDetails.getId().toString());
            assertThat(decoded.getClaim("roles").asList(String.class)).containsExactly("ROLE_COMMON");
        }

        @Test
        @DisplayName("Deve gerar JWT válido com CustomUserDetails direto")
        void shouldGenerateTokenFromUserDetailsDirectly() {
            // given
            CustomUserDetails userDetails = createMockUserDetails();

            // when
            String token = tokenProvider.generateToken(userDetails);

            // then
            assertThat(token).isNotBlank();
            var decoded = JWT.require(Algorithm.HMAC512(SECRET)).build().verify(token);
            assertThat(decoded.getSubject()).isEqualTo("joao@email.com");
        }

        @Test
        @DisplayName("Deve lançar InvalidAuthenticationException se Principal não for CustomUserDetails")
        void shouldThrowExceptionWhenPrincipalIsInvalid() {
            // given
            Authentication authentication = new UsernamePasswordAuthenticationToken("usuarioString", null);

            // when / then
            assertThatThrownBy(() -> tokenProvider.generateToken(authentication))
                    .isInstanceOf(InvalidAuthenticationException.class)
                    .hasMessageContaining("Principal is not an instance of CustomUserDetails");
        }
    }

    @Nested
    @DisplayName("validateToken / getUsernameFromToken")
    class ValidateToken {

        @Test
        @DisplayName("Deve retornar o email (subject) se o token for válido")
        void shouldReturnSubjectWhenTokenIsValid() {
            // given
            String token = tokenProvider.generateToken(createMockUserDetails());

            // when
            String subject = tokenProvider.validateToken(token);
            String username = tokenProvider.getUsernameFromToken(token);

            // then
            assertThat(subject).isEqualTo("joao@email.com");
            assertThat(username).isEqualTo("joao@email.com");
        }

        @Test
        @DisplayName("Deve retornar null se token for null no getUsernameFromToken")
        void shouldReturnNullWhenTokenIsNull() {
            // when
            String username = tokenProvider.getUsernameFromToken(null);

            // then
            assertThat(username).isNull();
        }

        @Test
        @DisplayName("Deve lançar InvalidAuthenticationException para token inválido")
        void shouldThrowExceptionForInvalidToken() {
            // given
            String invalidToken = "header.payload.signature_invalida";

            // when / then
            assertThatThrownBy(() -> tokenProvider.validateToken(invalidToken))
                    .isInstanceOf(InvalidAuthenticationException.class)
                    .hasMessageContaining("Invalid token");
        }

        @Test
        @DisplayName("Deve lançar InvalidAuthenticationException para token expirado")
        void shouldThrowExceptionForExpiredToken() {
            // given
            // Gerando um token expirado manualmente para o teste
            Algorithm algorithm = Algorithm.HMAC512(SECRET);
            String expiredToken = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject("joao@email.com")
                    .withExpiresAt(Instant.now().minusSeconds(3600)) // Expirou há 1 hora
                    .sign(algorithm);

            // when / then
            assertThatThrownBy(() -> tokenProvider.validateToken(expiredToken))
                    .isInstanceOf(InvalidAuthenticationException.class)
                    .hasMessageContaining("Invalid token");
        }
    }

    @Nested
    @DisplayName("Password Reset Token")
    class PasswordResetToken {

        @Test
        @DisplayName("Deve gerar token de reset de senha válido")
        void shouldGeneratePasswordResetToken() {
            // given
            String email = "joao@email.com";

            // when
            String token = tokenProvider.generatePasswordResetToken(email);

            // then
            assertThat(token).isNotBlank();
            var decoded = JWT.require(Algorithm.HMAC512(SECRET))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);

            assertThat(decoded.getSubject()).isEqualTo(email);
            assertThat(decoded.getClaim("type").asString()).isEqualTo("password-reset");
        }

        @Test
        @DisplayName("Deve recuperar email do token de reset de senha")
        void shouldGetEmailFromPasswordResetToken() {
            // given
            String email = "joao@email.com";
            String token = tokenProvider.generatePasswordResetToken(email);

            // when
            String extractedEmail = tokenProvider.getEmailFromPasswordResetToken(token);

            // then
            assertThat(extractedEmail).isEqualTo(email);
        }

        @Test
        @DisplayName("Deve lançar exceção se o token de reset não tiver a claim 'type' correta")
        void shouldThrowExceptionIfResetTokenHasWrongType() {
            // given
            // Gerando um JWT de auth normal (sem claim type = password-reset)
            String token = tokenProvider.generateToken(createMockUserDetails());

            // when / then
            assertThatThrownBy(() -> tokenProvider.getEmailFromPasswordResetToken(token))
                    .isInstanceOf(InvalidAuthenticationException.class)
                    .hasMessageContaining("Invalid password reset token");
        }

        @Test
        @DisplayName("Deve lançar exceção se o token de reset for inválido/malformado")
        void shouldThrowExceptionForInvalidResetToken() {
            // given
            String invalidToken = "invalido.123.abc";

            // when / then
            assertThatThrownBy(() -> tokenProvider.getEmailFromPasswordResetToken(invalidToken))
                    .isInstanceOf(InvalidAuthenticationException.class)
                    .hasMessageContaining("Invalid password reset token");
        }
    }
}
