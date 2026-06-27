package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.application.service.CommentService;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(
    name = "Comments",
    description = "Endpoints para gerenciamento de comentários em eventos. " +
                  "Todas as operações requerem autenticação via JWT Bearer token."
)
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    // ─── GET /comments/{id} ──────────────────────────────────────────────────

    @Operation(
        summary = "Buscar comentário por ID",
        description = "Retorna os dados completos de um comentário específico com base no seu identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário encontrado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommentResponseDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de comentário",
                    value = """
                        {
                          "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "content": "Que evento incrível! Mal posso esperar.",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "createdAt": "2025-06-01T15:30:00",
                          "updatedAt": "2025-06-01T16:00:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Unauthorized",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "status": "UNAUTHORIZED",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Comment not found with id: c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> findById(
            @Parameter(
                description = "Identificador único (UUID) do comentário",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        CommentResponseDTO comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    // ─── GET /comments/events/{eventId} ─────────────────────────────────────

    @Operation(
        summary = "Listar comentários de um evento",
        description = "Retorna de forma paginada todos os comentários associados a um evento específico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Página de comentários retornada com sucesso (pode ser vazia)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Exemplo de página de comentários",
                    value = """
                        {
                          "content": [
                            {
                              "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                              "content": "Que evento incrível! Mal posso esperar.",
                              "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                              "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                              "createdAt": "2025-06-01T15:30:00",
                              "updatedAt": "2025-06-01T15:30:00"
                            }
                          ],
                          "totalElements": 1,
                          "totalPages": 1,
                          "size": 20,
                          "number": 0
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Page<CommentResponseDTO>> findByEventId(
            @Parameter(
                description = "Identificador único (UUID) do evento cujos comentários serão listados",
                required = true,
                example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID eventId,
            @Parameter(hidden = true)
            @PageableDefault Pageable pageable) {
        Page<CommentResponseDTO> comments = commentService.findAllByEventId(eventId, pageable);
        return ResponseEntity.ok(comments);
    }

    // ─── POST /comments/events/{eventId} ────────────────────────────────────

    @Operation(
        summary = "Criar novo comentário",
        description = "Cria um novo comentário vinculado ao evento especificado. " +
                      "O autor é identificado automaticamente pelo token JWT informado no header."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Comentário criado com sucesso. O header `Location` contém a URI do novo recurso.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommentResponseDTO.class),
                examples = @ExampleObject(
                    name = "Comentário criado",
                    value = """
                        {
                          "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "content": "Adorei a proposta deste evento!",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "createdAt": "2025-06-01T18:00:00",
                          "updatedAt": "2025-06-01T18:00:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos — campo `content` em branco",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": ["message: Content must not be blank"],
                          "description": "uri=/comments/events/b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "status": "BAD_REQUEST",
                          "timestamp": "2025-06-01 18:00:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    @PostMapping("/events/{eventId}")
    public ResponseEntity<CommentResponseDTO> create(
            @Parameter(
                description = "Identificador único (UUID) do evento ao qual o comentário será vinculado",
                required = true,
                example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID eventId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do novo comentário",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CommentCreateDTO.class),
                    examples = @ExampleObject(
                        name = "Exemplo de payload",
                        value = """
                            {
                              "content": "Adorei a proposta deste evento!"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody CommentCreateDTO createDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponseDTO comment = commentService.create(createDTO, userDetails.getId(), eventId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comment.id())
                .toUri();

        return ResponseEntity.created(location).body(comment);
    }

    // ─── POST /comments/{commentId}/likes ────────────────────────────────────

    @Operation(
        summary = "Curtir comentário",
        description = "Registra um 'like' do usuário autenticado no comentário especificado. " +
                      "Caso o usuário já tenha curtido, a operação não tem efeito duplicado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Like registrado com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Comment not found with id: c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/likes",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 20:00:00"
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Void> likeComment(
            @Parameter(
                description = "Identificador único (UUID) do comentário a ser curtido",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {
        commentService.likeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }

    // ─── PUT /comments/{commentId} ───────────────────────────────────────────

    @Operation(
        summary = "Atualizar comentário",
        description = "Atualiza o conteúdo de um comentário existente. " +
                      "Apenas o autor original do comentário (identificado pelo JWT) pode editá-lo."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário atualizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommentResponseDTO.class),
                examples = @ExampleObject(
                    name = "Comentário atualizado",
                    value = """
                        {
                          "id": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "content": "Conteúdo atualizado: agora tenho ainda mais certeza que será incrível!",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "createdAt": "2025-06-01T15:30:00",
                          "updatedAt": "2025-06-01T19:45:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos — campo `content` em branco",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Comment not found with id: c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 19:45:00"
                        }
                        """
                )
            )
        )
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(
            @Parameter(
                description = "Identificador único (UUID) do comentário a ser atualizado",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novo conteúdo do comentário",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CommentCreateDTO.class),
                    examples = @ExampleObject(
                        name = "Exemplo de payload",
                        value = """
                            {
                              "content": "Conteúdo atualizado: agora tenho ainda mais certeza que será incrível!"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody CommentCreateDTO commentDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponseDTO updatedComment = commentService.update(commentId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    // ─── DELETE /comments/{commentId} ────────────────────────────────────────

    @Operation(
        summary = "Excluir comentário",
        description = "Remove permanentemente um comentário. " +
                      "Apenas o autor original (identificado pelo JWT) pode excluí-lo."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Comentário excluído com sucesso — sem conteúdo no corpo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Comment not found with id: c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 20:00:00"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @Parameter(
                description = "Identificador único (UUID) do comentário a ser excluído",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(hidden = true) @RequestAttribute String userId) {
        commentService.delete(commentId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    // ─── DELETE /comments/{commentId}/likes ──────────────────────────────────

    @Operation(
        summary = "Remover curtida do comentário",
        description = "Remove o 'like' do usuário autenticado no comentário especificado. " +
                      "Caso o usuário não tenha curtido previamente, a operação não tem efeito."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Like removido com sucesso"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comentário não encontrado para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Comment not found with id: c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/likes",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 20:00:00"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<Void> unlikeComment(
            @Parameter(
                description = "Identificador único (UUID) do comentário do qual a curtida será removida",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user) {
        commentService.unlikeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
}
