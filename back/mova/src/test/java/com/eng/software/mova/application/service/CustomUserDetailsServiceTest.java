package com.eng.software.mova.application.service;

import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService — Testes Unitários")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("Deve carregar usuário por email ou username com sucesso")
        void shouldLoadUserSuccessfully() {
            // given
            String identifier = "joao@email.com";
            User mockUser = User.builder()
                    .id(UUID.randomUUID())
                    .name("João da Silva")
                    .username("joaosilva")
                    .email("joao@email.com")
                    .password("encoded_password")
                    .userType(UserType.COMMON)
                    .build();

            given(userRepositoryPort.findByEmailOrUsername(identifier, identifier))
                    .willReturn(Optional.of(mockUser));

            // when
            UserDetails result = customUserDetailsService.loadUserByUsername(identifier);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(CustomUserDetails.class);

            CustomUserDetails customUserDetails = (CustomUserDetails) result;
            assertThat(customUserDetails.getId()).isEqualTo(mockUser.getId());
            assertThat(customUserDetails.getUsername()).isEqualTo(mockUser.getUsername());
            assertThat(customUserDetails.getPassword()).isEqualTo(mockUser.getPassword());
            assertThat(customUserDetails.getEmail()).isEqualTo(mockUser.getEmail());

            // Verifica as roles/authorities
            assertThat(customUserDetails.getAuthorities()).hasSize(1);
            GrantedAuthority authority = customUserDetails.getAuthorities().iterator().next();
            assertThat(authority.getAuthority()).isEqualTo("ROLE_COMMON");

            then(userRepositoryPort).should().findByEmailOrUsername(identifier, identifier);
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            String identifier = "inexistente";
            given(userRepositoryPort.findByEmailOrUsername(identifier, identifier))
                    .willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(identifier))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found: " + identifier);

            then(userRepositoryPort).should().findByEmailOrUsername(identifier, identifier);
        }
    }
}
