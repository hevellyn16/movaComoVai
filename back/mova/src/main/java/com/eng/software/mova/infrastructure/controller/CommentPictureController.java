package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.application.service.CommentPictureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/comments/{commentId}/pictures")
@RequiredArgsConstructor
@Tag(
    name = "Comment Pictures",
    description = "Endpoints para gerenciamento de imagens vinculadas a comentários. " +
                  "O upload é realizado via `multipart/form-data`. " +
                  "Todas as operações requerem autenticação via JWT Bearer token."
)
@SecurityRequirement(name = "bearerAuth")
public class CommentPictureController {

    private final CommentPictureService commentPictureService;

    // ─── GET /comments/{commentId}/pictures/{id} ──────────────────────────────

    @Operation(
        summary = "Buscar imagem de comentário por ID",
        description = "Retorna os dados de uma imagem específica vinculada a um comentário, " +
                      "com base no identificador do comentário e no identificador da imagem."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Imagem encontrada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommentPictureResponseDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de imagem de comentário",
                    value = """
                        {
                          "id": "e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "commentId": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "pictureUrl": "https://storage.example.com/comments/imagem01.jpg"
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
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures/e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "status": "UNAUTHORIZED",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Imagem ou comentário não encontrado para os IDs informados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "CommentPicture not found with id: e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures/e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentPictureResponseDTO> findById(
            @Parameter(
                description = "Identificador único (UUID) do comentário dono da imagem",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(
                description = "Identificador único (UUID) da imagem",
                required = true,
                example = "e1f2a3b4-c5d6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(commentPictureService.findById(commentId, id));
    }

    // ─── GET /comments/{commentId}/pictures ───────────────────────────────────

    @Operation(
        summary = "Listar imagens de um comentário",
        description = "Retorna de forma paginada todas as imagens vinculadas a um comentário específico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Página de imagens retornada com sucesso (pode ser vazia)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Exemplo de página de imagens",
                    value = """
                        {
                          "content": [
                            {
                              "id": "e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                              "commentId": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                              "pictureUrl": "https://storage.example.com/comments/imagem01.jpg"
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
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<Page<CommentPictureResponseDTO>> findByCommentId(
            @Parameter(
                description = "Identificador único (UUID) do comentário cujas imagens serão listadas",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(hidden = true)
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentPictureService.findByCommentId(commentId, pageable));
    }

    // ─── POST /comments/{commentId}/pictures ──────────────────────────────────

    @Operation(
        summary = "Fazer upload de imagem para um comentário",
        description = "Realiza o upload de um arquivo de imagem e o vincula ao comentário especificado. " +
                      "A requisição deve ser enviada como `multipart/form-data` com o campo `file` contendo o arquivo. " +
                      "O header `Location` da resposta conterá a URI da imagem criada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Imagem enviada e vinculada com sucesso. O header `Location` contém a URI do novo recurso.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CommentPictureResponseDTO.class),
                examples = @ExampleObject(
                    name = "Imagem criada",
                    value = """
                        {
                          "id": "e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "commentId": "c1a2b3d4-e5f6-7890-abcd-ef1234567890",
                          "pictureUrl": "https://storage.example.com/comments/imagem01.jpg"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Arquivo ausente ou formato inválido",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Required request part 'file' is not present",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures",
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
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 18:00:00"
                        }
                        """
                )
            )
        )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentPictureResponseDTO> uploadPicture(
            @Parameter(
                description = "Identificador único (UUID) do comentário ao qual a imagem será vinculada",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(
                description = "Arquivo de imagem a ser enviado (JPEG, PNG, etc.)",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        CommentPictureResponseDTO createdPicture = commentPictureService.savePicture(commentId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPicture.id())
                .toUri();

        return ResponseEntity.created(location).body(createdPicture);
    }

    // ─── DELETE /comments/{commentId}/pictures/{id} ───────────────────────────

    @Operation(
        summary = "Excluir imagem de um comentário",
        description = "Remove permanentemente uma imagem vinculada a um comentário. " +
                      "O comentário e a imagem devem existir e estar relacionados."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Imagem excluída com sucesso — sem conteúdo no corpo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Imagem ou comentário não encontrado para os IDs informados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "CommentPicture not found with id: e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "description": "uri=/comments/c1a2b3d4-e5f6-7890-abcd-ef1234567890/pictures/e1f2a3b4-c5d6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 20:00:00"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(
                description = "Identificador único (UUID) do comentário dono da imagem",
                required = true,
                example = "c1a2b3d4-e5f6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID commentId,
            @Parameter(
                description = "Identificador único (UUID) da imagem a ser excluída",
                required = true,
                example = "e1f2a3b4-c5d6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        commentPictureService.deleteById(commentId, id);
        return ResponseEntity.noContent().build();
    }
}
