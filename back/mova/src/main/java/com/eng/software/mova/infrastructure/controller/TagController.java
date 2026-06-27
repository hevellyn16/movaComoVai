package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.application.service.TagService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Gerenciamento de tags de interesse: consulta, criação, atualização e exclusão. "
        + "Tags são usadas para categorizar eventos e preferências de usuários.")
public class TagController {
    private final TagService tagService;

    @Operation(
            summary = "Listar todas as tags",
            description = "Retorna a lista completa de tags disponíveis no sistema. "
                    + "Usado para onboarding, filtros de busca e administração. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tags retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> findAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @Operation(
            summary = "Buscar tag por ID",
            description = "Retorna os dados de uma tag específica pelo seu UUID. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = TagResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> findById(
            @Parameter(description = "UUID da tag", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(tagService.findById(id));
    }

    @Operation(
            summary = "Criar nova tag",
            description = "Cadastra uma nova tag no sistema. O nome da tag deve ser único. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TagResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (nome vazio ou fora do tamanho)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe uma tag com esse nome", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDTO> create(@Valid @RequestBody TagCreateDTO dto) {
        TagResponseDTO createdTag = tagService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTag.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTag);
    }

    @Operation(
            summary = "Atualizar tag",
            description = "Atualiza o nome de uma tag existente. O novo nome deve ser único. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TagResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe uma tag com esse nome", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDTO> update(
            @Parameter(description = "UUID da tag a ser atualizada", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Valid @RequestBody TagUpdateDTO dto) {
        return ResponseEntity.ok(tagService.update(id, dto));
    }

    @Operation(
            summary = "Excluir tag",
            description = "Remove uma tag do sistema permanentemente. Isso também remove as associações com usuários e eventos. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID da tag a ser excluída", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
