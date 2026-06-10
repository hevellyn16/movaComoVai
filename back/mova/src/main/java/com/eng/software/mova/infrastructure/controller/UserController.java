package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.tag.UserTagAssociationDTO;
import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserPublicProfileDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.application.dto.auth.EmailRequestDTO;
import com.eng.software.mova.application.dto.auth.PasswordRequestDTO;
import com.eng.software.mova.application.service.UserService;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários: cadastro, atualização, perfil público, avatar e preferências de tags.")
public class UserController {
    private final UserService userService;

    // ======================== ROTAS ADMIN ========================

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados completos de um usuário pelo seu UUID. **Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(
            @Parameter(description = "UUID do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(
            summary = "Buscar usuário por e-mail",
            description = "Retorna os dados completos de um usuário pelo seu e-mail. **Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findByEmail(
            @Parameter(description = "E-mail do usuário", example = "usuario@email.com")
            @PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista paginada de todos os usuários cadastrados. **Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(
            summary = "Promover usuário para ADMIN",
            description = "Altera o tipo do usuário de COMMON para ADMIN. **Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário promovido com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateToAdmin(
            @Parameter(description = "UUID do usuário a ser promovido", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.updateToAdmin(id));
    }

    // ======================== ROTAS PÚBLICAS ========================

    @Operation(
            summary = "Cadastrar novo usuário",
            description = "Cria uma nova conta de usuário com tipo COMMON. **Acesso: público (sem autenticação).**",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (validação falhou)", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail ou username já cadastrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO userCreated = userService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userCreated.id())
                .toUri();

        return ResponseEntity.created(location).body(userCreated);
    }

    @Operation(
            summary = "Solicitar recuperação de senha",
            description = "Envia um e-mail com link de recuperação de senha para o endereço informado. **Acesso: público (sem autenticação).**",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "E-mail de recuperação enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "E-mail inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado com o e-mail informado", content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody EmailRequestDTO dto) {
        userService.forgotPassword(dto.email());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Redefinir senha",
            description = "Redefine a senha do usuário utilizando o token de recuperação recebido por e-mail. **Acesso: público (sem autenticação).**",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senhas não coincidem ou token inválido/expirado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Token de recuperação de senha recebido por e-mail")
            @RequestParam String token,
            @Valid @RequestBody PasswordRequestDTO dto) {
        userService.resetPassword(token, dto.password(), dto.confirmPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Visualizar perfil público de um usuário",
            description = "Retorna as informações públicas do perfil de um usuário pelo username. "
                    + "Não exibe dados sensíveis (e-mail, senha, configurações de notificação). "
                    + "Retorna 403 se o perfil for privado. **Acesso: público (sem autenticação).**",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil público retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserPublicProfileDTO.class))),
            @ApiResponse(responseCode = "403", description = "O perfil deste usuário é privado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado com o username informado", content = @Content)
    })
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserPublicProfileDTO> getPublicProfile(
            @Parameter(description = "Username do usuário", example = "joaosilva")
            @PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicProfile(username));
    }

    // ======================== ROTAS AUTENTICADAS (USUÁRIO) ========================

    @Operation(
            summary = "Atualizar dados do usuário autenticado",
            description = "Atualiza os dados do usuário que está autenticado (identificado pelo token JWT). "
                    + "Todos os campos são opcionais — envie apenas os que deseja alterar. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail ou username já em uso por outro usuário", content = @Content)
    })
    @PutMapping
    public ResponseEntity<UserResponseDTO> update(
            @Valid @RequestBody UserUpdateDTO dto,
            @Parameter(hidden = true) @RequestAttribute String userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(userService.update(UUID.fromString(userId), dto));
    }

    @Operation(
            summary = "Excluir conta do usuário autenticado",
            description = "Realiza a exclusão lógica (soft delete) da conta do usuário autenticado, "
                    + "marcando-o como inativo no banco de dados. **Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(
            @Parameter(hidden = true) @RequestAttribute String userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        userService.delete(UUID.fromString(userId));

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Associar tags de interesse ao usuário",
            description = "Associa uma ou mais tags ao perfil do usuário autenticado, representando seus interesses. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tags associadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário ou tag(s) não encontrado(s)", content = @Content)
    })
    @PostMapping("/me/tags")
    public ResponseEntity<Void> addTagToUser(
            @RequestBody UserTagAssociationDTO dto,
            @Parameter(hidden = true) @RequestAttribute String userId) {
        userService.addTagToUser(UUID.fromString(userId), dto.tagIds());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover tag de interesse do usuário",
            description = "Remove uma tag específica do perfil do usuário autenticado. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário ou tag não encontrado(s)", content = @Content)
    })
    @DeleteMapping("/me/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromUser(
            @Parameter(description = "UUID da tag a ser removida", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID tagId,
            @Parameter(hidden = true) @RequestAttribute String userId) {

        userService.removeTagFromUser(UUID.fromString(userId), tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Fazer upload da foto de perfil (avatar)",
            description = "Faz upload de uma imagem para ser usada como foto de perfil do usuário autenticado. "
                    + "O arquivo deve ser uma imagem (image/*) com tamanho máximo de 5MB. "
                    + "A URL pública do avatar é retornada na resposta e salva automaticamente no perfil do usuário. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar enviado com sucesso",
                    content = @Content(schema = @Schema(example = "{\"avatarUrl\": \"http://localhost:8080/uploads/avatars/uuid_123456.png\"}"))),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio ou formato inválido (somente imagens)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno ao salvar o arquivo", content = @Content)
    })
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "Arquivo de imagem do avatar (PNG, JPG, WEBP, etc.)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(hidden = true) @RequestAttribute String userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String avatarUrl = userService.uploadAvatar(UUID.fromString(userId), file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }
}
