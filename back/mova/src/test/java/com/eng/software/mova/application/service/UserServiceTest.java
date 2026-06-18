package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserPublicProfileDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.EmailGateway;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.factory.UserFactory;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.shared.exceptions.ApiException;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários do {@link UserService}.
 * <p>
 * Utiliza Mockito com BDD style (given/when/then) e AssertJ para asserções fluidas.
 * Cada método público do service possui um {@link Nested} group próprio
 * para facilitar leitura e manutenção.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — Testes Unitários")
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private Auth0JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailGateway emailGateway;

    @Mock
    private TagService tagService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    // ======================== findById ========================

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve retornar UserResponseDTO quando o usuário existe")
        void shouldReturnUserResponseWhenUserExists() {
            // given
            User user = UserFactory.createDefaultUser();
            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));

            // when
            UserResponseDTO result = userService.findById(UserFactory.DEFAULT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(UserFactory.DEFAULT_ID);
            assertThat(result.name()).isEqualTo(UserFactory.DEFAULT_NAME);
            assertThat(result.email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            assertThat(result.username()).isEqualTo(UserFactory.DEFAULT_USERNAME);
            assertThat(result.userType()).isEqualTo(UserType.COMMON.name());

            then(userRepositoryPort).should(times(1)).findById(UserFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowResourceNotFoundWhenUserDoesNotExist() {
            // given
            UUID nonExistentId = UUID.randomUUID();
            given(userRepositoryPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.findById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + nonExistentId);
        }
    }

    // ======================== findByEmail ========================

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("Deve retornar UserResponseDTO quando o email existe")
        void shouldReturnUserResponseWhenEmailExists() {
            // given
            User user = UserFactory.createDefaultUser();
            given(userRepositoryPort.findByEmail(UserFactory.DEFAULT_EMAIL)).willReturn(Optional.of(user));

            // when
            UserResponseDTO result = userService.findByEmail(UserFactory.DEFAULT_EMAIL);

            // then
            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            then(userRepositoryPort).should().findByEmail(UserFactory.DEFAULT_EMAIL);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o email não existe")
        void shouldThrowResourceNotFoundWhenEmailDoesNotExist() {
            // given
            String email = "inexistente@email.com";
            given(userRepositoryPort.findByEmail(email)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.findByEmail(email))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with email: " + email);
        }
    }

    // ======================== findAll ========================

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Deve retornar página de usuários")
        void shouldReturnPageOfUsers() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            User user1 = UserFactory.createDefaultUser();
            User user2 = UserFactory.createUserWithIdAndEmail(UUID.randomUUID(), "maria@email.com");
            Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

            given(userRepositoryPort.findAll(pageable)).willReturn(userPage);

            // when
            Page<UserResponseDTO> result = userService.findAll(pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent().get(0).email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            assertThat(result.getContent().get(1).email()).isEqualTo("maria@email.com");

            then(userRepositoryPort).should().findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há usuários")
        void shouldReturnEmptyPageWhenNoUsers() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(userRepositoryPort.findAll(pageable)).willReturn(emptyPage);

            // when
            Page<UserResponseDTO> result = userService.findAll(pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ======================== create ========================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar usuário com sucesso quando email e username são únicos")
        void shouldCreateUserSuccessfully() {
            // given
            UserCreateDTO dto = UserFactory.createDefaultUserCreateDTO();

            given(userRepositoryPort.existsByEmail(dto.email())).willReturn(false);
            given(userRepositoryPort.existsByUsername(dto.username())).willReturn(false);
            given(encoder.encode(dto.password())).willReturn(UserFactory.DEFAULT_ENCODED_PASSWORD);
            given(userRepositoryPort.save(any(User.class))).willAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId(UserFactory.DEFAULT_ID);
                return saved;
            });

            // when
            UserResponseDTO result = userService.create(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(UserFactory.DEFAULT_NAME);
            assertThat(result.email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            assertThat(result.userType()).isEqualTo(UserType.COMMON.name());
            assertThat(result.isActive()).isTrue();

            then(userRepositoryPort).should().existsByEmail(dto.email());
            then(userRepositoryPort).should().existsByUsername(dto.username());
            then(encoder).should().encode(dto.password());
            then(userRepositoryPort).should().save(any(User.class));
        }

        @Test
        @DisplayName("Deve codificar a senha antes de salvar")
        void shouldEncodePasswordBeforeSaving() {
            // given
            UserCreateDTO dto = UserFactory.createDefaultUserCreateDTO();
            given(userRepositoryPort.existsByEmail(anyString())).willReturn(false);
            given(userRepositoryPort.existsByUsername(anyString())).willReturn(false);
            given(encoder.encode(dto.password())).willReturn(UserFactory.DEFAULT_ENCODED_PASSWORD);
            given(userRepositoryPort.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            userService.create(dto);

            // then
            then(userRepositoryPort).should().save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getPassword()).isEqualTo(UserFactory.DEFAULT_ENCODED_PASSWORD);
            assertThat(savedUser.getPassword()).isNotEqualTo(dto.password());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando email já existe")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // given
            UserCreateDTO dto = UserFactory.createDefaultUserCreateDTO();
            given(userRepositoryPort.existsByEmail(dto.email())).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.create(dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with email: " + dto.email());

            then(userRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando username já existe")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            // given
            UserCreateDTO dto = UserFactory.createDefaultUserCreateDTO();
            given(userRepositoryPort.existsByEmail(dto.email())).willReturn(false);
            given(userRepositoryPort.existsByUsername(dto.username())).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.create(dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Username already taken: " + dto.username());

            then(userRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Deve atualizar todos os campos do usuário com sucesso")
        void shouldUpdateUserSuccessfully() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createFullUpdateDTO();

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail(anyString())).willReturn(false);
            given(userRepositoryPort.existsByUsername(anyString())).willReturn(false);
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            UserResponseDTO result = userService.update(UserFactory.DEFAULT_ID, dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("João Atualizado");
            assertThat(result.username()).isEqualTo("joao_novo");
            assertThat(result.email()).isEqualTo("joao.novo@email.com");

            then(userRepositoryPort).should().update(any(User.class));
            then(userRepositoryPort).should().existsByEmail("joao.novo@email.com");
            then(userRepositoryPort).should().existsByUsername("joao_novo");
        }

        @Test
        @DisplayName("Deve atualizar apenas o nome quando somente nome é fornecido")
        void shouldUpdateOnlyNameWhenOnlyNameProvided() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createNameOnlyUpdateDTO("Novo Nome");

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            UserResponseDTO result = userService.update(UserFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.name()).isEqualTo("Novo Nome");
            assertThat(result.email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            assertThat(result.username()).isEqualTo(UserFactory.DEFAULT_USERNAME);
            
            then(userRepositoryPort).should(never()).existsByEmail(anyString());
            then(userRepositoryPort).should(never()).existsByUsername(anyString());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            UserUpdateDTO dto = UserFactory.createNameOnlyUpdateDTO("Novo Nome");

            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.update(id, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + id);

            then(userRepositoryPort).should(never()).update(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo email já existe")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createEmailOnlyUpdateDTO("outro@email.com");

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(userRepositoryPort.existsByEmail("outro@email.com")).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.update(UserFactory.DEFAULT_ID, dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with email: outro@email.com");

            then(userRepositoryPort).should(never()).update(any(User.class));
        }

        @Test
        @DisplayName("Deve permitir update quando o email não foi alterado")
        void shouldAllowUpdateWhenEmailUnchanged() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createEmailOnlyUpdateDTO(UserFactory.DEFAULT_EMAIL);

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            UserResponseDTO result = userService.update(UserFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.email()).isEqualTo(UserFactory.DEFAULT_EMAIL);
            then(userRepositoryPort).should(never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo username já existe")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createUsernameOnlyUpdateDTO("username_ocupado");

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            // Email não foi alterado na DTO, então retorna false ou não chega a verificar
            given(userRepositoryPort.existsByUsername("username_ocupado")).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.update(UserFactory.DEFAULT_ID, dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Username already taken: username_ocupado");

            then(userRepositoryPort).should(never()).update(any(User.class));
        }

        @Test
        @DisplayName("Deve permitir update quando o username não foi alterado")
        void shouldAllowUpdateWhenUsernameUnchanged() {
            // given
            User existing = UserFactory.createDefaultUser();
            UserUpdateDTO dto = UserFactory.createUsernameOnlyUpdateDTO(UserFactory.DEFAULT_USERNAME);

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            UserResponseDTO result = userService.update(UserFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.username()).isEqualTo(UserFactory.DEFAULT_USERNAME);
            then(userRepositoryPort).should(never()).existsByUsername(anyString());
        }
    }

    // ======================== updateToAdmin ========================

    @Nested
    @DisplayName("updateToAdmin")
    class UpdateToAdmin {

        @Test
        @DisplayName("Deve promover um usuário COMMON para ADMIN")
        void shouldPromoteUserToAdmin() {
            // given
            User user = UserFactory.createDefaultUser();
            assertThat(user.getUserType()).isEqualTo(UserType.COMMON);

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            UserResponseDTO result = userService.updateToAdmin(UserFactory.DEFAULT_ID);

            // then
            assertThat(result.userType()).isEqualTo(UserType.ADMIN.name());

            then(userRepositoryPort).should().update(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getUserType()).isEqualTo(UserType.ADMIN);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.updateToAdmin(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + id);
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar o usuário quando ele existe")
        void shouldDeleteUserWhenExists() {
            // given
            given(userRepositoryPort.existsById(UserFactory.DEFAULT_ID)).willReturn(true);

            // when
            userService.delete(UserFactory.DEFAULT_ID);

            // then
            then(userRepositoryPort).should().deleteById(UserFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(userRepositoryPort.existsById(id)).willReturn(false);

            // when / then
            assertThatThrownBy(() -> userService.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + id);

            then(userRepositoryPort).should(never()).deleteById(any());
        }
    }

    // ======================== forgotPassword ========================

    @Nested
    @DisplayName("forgotPassword")
    class ForgotPassword {

        @Test
        @DisplayName("Deve enviar email de recuperação com link de reset")
        void shouldSendPasswordResetEmail() {
            // given
            User user = UserFactory.createDefaultUser();
            String resetToken = "jwt-reset-token-abc123";

            ReflectionTestUtils.setField(userService, "passwordResetBaseUrl", "http://localhost:3000");

            given(userRepositoryPort.findByEmail(UserFactory.DEFAULT_EMAIL)).willReturn(Optional.of(user));
            given(jwtTokenProvider.generatePasswordResetToken(UserFactory.DEFAULT_EMAIL)).willReturn(resetToken);

            // when
            userService.forgotPassword(UserFactory.DEFAULT_EMAIL);

            // then
            then(jwtTokenProvider).should().generatePasswordResetToken(UserFactory.DEFAULT_EMAIL);
            then(emailGateway).should().sendHtmlEmail(
                    eq(UserFactory.DEFAULT_EMAIL),
                    eq("Recuperacao de senha"),
                    eq("reset-password"),
                    anyMap()
            );
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o email não existe")
        void shouldThrowExceptionWhenEmailNotFound() {
            // given
            String email = "inexistente@email.com";
            given(userRepositoryPort.findByEmail(email)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.forgotPassword(email))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with email: " + email);

            then(emailGateway).shouldHaveNoInteractions();
        }
    }

    // ======================== resetPassword ========================

    @Nested
    @DisplayName("resetPassword")
    class ResetPassword {

        @Test
        @DisplayName("Deve redefinir a senha quando token é válido e senhas coincidem")
        void shouldResetPasswordSuccessfully() {
            // given
            User user = UserFactory.createDefaultUser();
            String token = "valid-token";
            String newPassword = "NovaSenha123";
            String encodedNew = "$2a$10$newEncodedPasswordHash";

            given(jwtTokenProvider.getEmailFromPasswordResetToken(token)).willReturn(UserFactory.DEFAULT_EMAIL);
            given(userRepositoryPort.findByEmail(UserFactory.DEFAULT_EMAIL)).willReturn(Optional.of(user));
            given(encoder.encode(newPassword)).willReturn(encodedNew);

            // when
            userService.resetPassword(token, newPassword, newPassword);

            // then
            then(userRepositoryPort).should().update(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo(encodedNew);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar ApiException quando senha e confirmação não coincidem")
        void shouldThrowExceptionWhenPasswordsDoNotMatch() {
            // given
            String token = "valid-token";

            // when / then
            assertThatThrownBy(() -> userService.resetPassword(token, "Senha123", "SenhaDiferente"))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("Password and confirm password do not match");

            then(jwtTokenProvider).shouldHaveNoInteractions();
            then(userRepositoryPort).should(never()).update(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando email do token não corresponde a nenhum usuário")
        void shouldThrowExceptionWhenTokenEmailNotFound() {
            // given
            String token = "valid-token";
            String password = "NovaSenha123";

            given(jwtTokenProvider.getEmailFromPasswordResetToken(token)).willReturn("ghost@email.com");
            given(userRepositoryPort.findByEmail("ghost@email.com")).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.resetPassword(token, password, password))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with email: ghost@email.com");
        }
    }

    // ======================== addTagToUser ========================

    @Nested
    @DisplayName("addTagToUser")
    class AddTagToUser {

        @Test
        @DisplayName("Deve associar tags ao usuário com sucesso")
        void shouldAddTagsSuccessfully() {
            // given
            UUID tagId1 = UUID.randomUUID();
            UUID tagId2 = UUID.randomUUID();
            Set<UUID> tagIds = Set.of(tagId1, tagId2);

            User user = UserFactory.createDefaultUser();

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            willDoNothing().given(tagService).verifyAllTagsExist(tagIds);

            // when
            userService.addTagToUser(UserFactory.DEFAULT_ID, tagIds);

            // then
            then(tagService).should().verifyAllTagsExist(tagIds);
            then(userRepositoryPort).should().update(userCaptor.capture());

            User captured = userCaptor.getValue();
            assertThat(captured.getTagsId()).containsAll(tagIds);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            Set<UUID> tagIds = Set.of(UUID.randomUUID());

            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.addTagToUser(id, tagIds))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + id);

            then(userRepositoryPort).should(never()).update(any());
        }

        @Test
        @DisplayName("Deve propagar exceção quando tag não existe")
        void shouldPropagateExceptionWhenTagDoesNotExist() {
            // given
            Set<UUID> tagIds = Set.of(UUID.randomUUID());
            User user = UserFactory.createDefaultUser();

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            willThrow(new ResourceNotFoundException("One or more tags not found with ids: " + tagIds))
                    .given(tagService).verifyAllTagsExist(tagIds);

            // when / then
            assertThatThrownBy(() -> userService.addTagToUser(UserFactory.DEFAULT_ID, tagIds))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("One or more tags not found");

            then(userRepositoryPort).should(never()).update(any());
        }
    }

    // ======================== removeTagFromUser ========================

    @Nested
    @DisplayName("removeTagFromUser")
    class RemoveTagFromUser {

        @Test
        @DisplayName("Deve remover tag do usuário quando ela está associada")
        void shouldRemoveTagSuccessfully() {
            // given
            UUID tagId = UUID.randomUUID();
            User user = UserFactory.createUserWithTags(Set.of(tagId));

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            willDoNothing().given(tagService).verifyAllTagsExist(Set.of(tagId));

            // when
            userService.removeTagFromUser(UserFactory.DEFAULT_ID, tagId);

            // then
            then(userRepositoryPort).should().update(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getTagsId()).doesNotContain(tagId);
        }

        @Test
        @DisplayName("Não deve chamar update quando a tag não estava associada ao usuário")
        void shouldNotUpdateWhenTagWasNotAssociated() {
            // given
            UUID tagId = UUID.randomUUID();
            UUID otherTagId = UUID.randomUUID();
            User user = UserFactory.createUserWithTags(Set.of(otherTagId));

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            willDoNothing().given(tagService).verifyAllTagsExist(Set.of(tagId));

            // when
            userService.removeTagFromUser(UserFactory.DEFAULT_ID, tagId);

            // then
            then(userRepositoryPort).should(never()).update(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            UUID tagId = UUID.randomUUID();

            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.removeTagFromUser(id, tagId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ======================== getPublicProfile ========================

    @Nested
    @DisplayName("getPublicProfile")
    class GetPublicProfile {

        @Test
        @DisplayName("Deve retornar perfil público quando perfil não é privado")
        void shouldReturnPublicProfileWhenNotPrivate() {
            // given
            User user = UserFactory.createDefaultUser();
            given(userRepositoryPort.findByUsername(UserFactory.DEFAULT_USERNAME)).willReturn(Optional.of(user));

            // when
            UserPublicProfileDTO result = userService.getPublicProfile(UserFactory.DEFAULT_USERNAME);

            // then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(UserFactory.DEFAULT_USERNAME);
            assertThat(result.name()).isEqualTo(UserFactory.DEFAULT_NAME);
            assertThat(result.bio()).isEqualTo(UserFactory.DEFAULT_BIO);
            assertThat(result.location()).isEqualTo(UserFactory.DEFAULT_LOCATION);
            assertThat(result.avatarUrl()).isEqualTo(UserFactory.DEFAULT_AVATAR_URL);
        }

        @Test
        @DisplayName("Deve lançar ApiException com status FORBIDDEN quando o perfil é privado")
        void shouldThrowForbiddenWhenProfileIsPrivate() {
            // given
            User privateUser = UserFactory.createPrivateUser();
            given(userRepositoryPort.findByUsername(UserFactory.DEFAULT_USERNAME)).willReturn(Optional.of(privateUser));

            // when / then
            assertThatThrownBy(() -> userService.getPublicProfile(UserFactory.DEFAULT_USERNAME))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("This profile is private")
                    .extracting(ex -> ((ApiException) ex).getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o username não existe")
        void shouldThrowExceptionWhenUsernameNotFound() {
            // given
            String username = "inexistente";
            given(userRepositoryPort.findByUsername(username)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.getPublicProfile(username))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with username: " + username);
        }
    }

    // ======================== uploadAvatar ========================

    @Nested
    @DisplayName("uploadAvatar")
    class UploadAvatar {

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID id = UUID.randomUUID();
            MockMultipartFile file = new MockMultipartFile(
                    "file", "avatar.png", "image/png", "fake-image-data".getBytes()
            );
            given(userRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.uploadAvatar(id, file))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + id);
        }

        @Test
        @DisplayName("Deve lançar ApiException quando arquivo está vazio")
        void shouldThrowExceptionWhenFileIsEmpty() {
            // given
            User user = UserFactory.createDefaultUser();
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "avatar.png", "image/png", new byte[0]
            );
            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));

            // when / then
            assertThatThrownBy(() -> userService.uploadAvatar(UserFactory.DEFAULT_ID, emptyFile))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("Avatar file must not be empty");
        }

        @Test
        @DisplayName("Deve lançar ApiException quando arquivo não é imagem")
        void shouldThrowExceptionWhenFileIsNotImage() {
            // given
            User user = UserFactory.createDefaultUser();
            MockMultipartFile textFile = new MockMultipartFile(
                    "file", "document.pdf", "application/pdf", "fake-pdf-data".getBytes()
            );
            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));

            // when / then
            assertThatThrownBy(() -> userService.uploadAvatar(UserFactory.DEFAULT_ID, textFile))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("Only image files are allowed");
        }

        @Test
        @DisplayName("Deve fazer upload do avatar com sucesso e retornar URL")
        void shouldUploadAvatarSuccessfully() {
            // given
            User user = UserFactory.createDefaultUser();
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file", "photo.jpg", "image/jpeg", "fake-image-binary".getBytes()
            );

            // Configura diretório temporário para upload dentro do workspace
            String tempDir = System.getProperty("java.io.tmpdir") + "/mova-test-avatars";
            ReflectionTestUtils.setField(userService, "avatarUploadDir", tempDir);
            ReflectionTestUtils.setField(userService, "avatarBaseUrl", "http://localhost:8080/uploads/avatars");

            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));
            given(userRepositoryPort.update(any(User.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            String avatarUrl = userService.uploadAvatar(UserFactory.DEFAULT_ID, imageFile);

            // then
            assertThat(avatarUrl).startsWith("http://localhost:8080/uploads/avatars/");
            assertThat(avatarUrl).contains(UserFactory.DEFAULT_ID.toString());
            assertThat(avatarUrl).endsWith(".jpg");

            then(userRepositoryPort).should().update(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getAvatarUrl()).isEqualTo(avatarUrl);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar ApiException quando contentType é null")
        void shouldThrowExceptionWhenContentTypeIsNull() {
            // given
            User user = UserFactory.createDefaultUser();
            MockMultipartFile fileWithoutType = new MockMultipartFile(
                    "file", "avatar.png", null, "fake-data".getBytes()
            );
            given(userRepositoryPort.findById(UserFactory.DEFAULT_ID)).willReturn(Optional.of(user));

            // when / then
            assertThatThrownBy(() -> userService.uploadAvatar(UserFactory.DEFAULT_ID, fileWithoutType))
                    .isInstanceOf(ApiException.class)
                    .hasMessageContaining("Only image files are allowed");
        }
    }
}
