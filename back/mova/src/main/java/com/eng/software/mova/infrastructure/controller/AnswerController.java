package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.application.service.AnswerService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@Tag(
    name = "Answers",
    description = "Endpoints para gerenciamento de respostas a comentários. " +
                  "Todas as operações requerem autenticação via JWT Bearer token."
)
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {

    private final AnswerService answerService;

    // ─── GET /answers/{id} ───────────────────────────────────────────────────

    @Operation(
        summary = "Buscar resposta por ID",
        description = "Retorna os dados completos de uma resposta específica com base no seu identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Resposta encontrada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AnswerResponseDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de resposta",
                    value = """
                        {
                          "id": "d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "answer": "Ótima pergunta! O evento começa às 19h.",
                          "createdAt": "2025-06-01T15:30:00",
                          "updatedAt": "2025-06-01T16:00:00",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "commentId": "f0e1d2c3-b4a5-9678-efab-cd1234567890"
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
                          "description": "uri=/answers/d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "status": "UNAUTHORIZED",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Resposta não encontrada para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Answer not found with id: d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "description": "uri=/answers/d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> findById(
            @Parameter(
                description = "Identificador único (UUID) da resposta",
                required = true,
                example = "d1e2f3a4-b5c6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        AnswerResponseDTO answerResponseDTO = answerService.findById(id);
        return ResponseEntity.ok(answerResponseDTO);
    }

    // ─── GET /answers/comments/{commentId} ──────────────────────────────────

    @Operation(
        summary = "Listar respostas de um comentário",
        description = "Retorna todas as respostas associadas a um comentário específico, sem paginação."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de respostas retornada com sucesso (pode ser vazia)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Exemplo de página de respostas",
                    value = """
                        {
                          "content": [
                            {
                              "id": "d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                              "answer": "Ótima pergunta! O evento começa às 19h.",
                              "createdAt": "2025-06-01T15:30:00",
                              "updatedAt": "2025-06-01T15:30:00",
                              "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                              "commentId": "f0e1d2c3-b4a5-9678-efab-cd1234567890"
                            }
                          ],
                          "totalElements": 1,
                          "totalPages": 1,
                          "size": 1,
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
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Page<AnswerResponseDTO>> findByCommentId(
            @Parameter(
                description = "Identificador único (UUID) do comentário cujas respostas serão listadas",
                required = true,
                example = "f0e1d2c3-b4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID commentId) {
        Page<AnswerResponseDTO> answers = answerService.findByCommentId(commentId, Pageable.unpaged());
        return ResponseEntity.ok(answers);
    }

    // ─── POST /answers/{commentId} ───────────────────────────────────────────

    @Operation(
        summary = "Criar nova resposta",
        description = "Cria uma nova resposta para o comentário especificado. " +
                      "O autor é identificado automaticamente pelo token JWT informado no header."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Resposta criada com sucesso. O header `Location` contém a URI do novo recurso.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AnswerResponseDTO.class),
                examples = @ExampleObject(
                    name = "Resposta criada",
                    value = """
                        {
                          "id": "d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "answer": "O evento terá buffet e música ao vivo!",
                          "createdAt": "2025-06-01T18:00:00",
                          "updatedAt": "2025-06-01T18:00:00",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "commentId": "f0e1d2c3-b4a5-9678-efab-cd1234567890"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos — campo `answer` em branco",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": ["message: Answer must not be blank"],
                          "description": "uri=/answers/f0e1d2c3-b4a5-9678-efab-cd1234567890",
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
    @PostMapping("/{commentId}")
    public ResponseEntity<AnswerResponseDTO> create(
            @Parameter(
                description = "Identificador único (UUID) do comentário ao qual a resposta será vinculada",
                required = true,
                example = "f0e1d2c3-b4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da nova resposta",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AnswerCreateDTO.class),
                    examples = @ExampleObject(
                        name = "Exemplo de payload",
                        value = """
                            {
                              "answer": "O evento terá buffet e música ao vivo!"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody AnswerCreateDTO answerCreateDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        AnswerResponseDTO createdAnswer = answerService.create(answerCreateDTO, userDetails.getId(), commentId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdAnswer.id())
                .toUri();

        return ResponseEntity.created(location).body(createdAnswer);
    }

    // ─── PUT /answers/{answerId} ─────────────────────────────────────────────

    @Operation(
        summary = "Atualizar resposta",
        description = "Atualiza o conteúdo de uma resposta existente. " +
                      "Apenas o autor original da resposta (identificado pelo JWT) pode editá-la."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Resposta atualizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AnswerResponseDTO.class),
                examples = @ExampleObject(
                    name = "Resposta atualizada",
                    value = """
                        {
                          "id": "d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "answer": "Conteúdo corrigido: o evento inicia às 20h, não 19h.",
                          "createdAt": "2025-06-01T15:30:00",
                          "updatedAt": "2025-06-01T19:45:00",
                          "userId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                          "commentId": "f0e1d2c3-b4a5-9678-efab-cd1234567890"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos — campo `answer` em branco",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Resposta não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Answer not found with id: d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "description": "uri=/answers/d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 19:45:00"
                        }
                        """
                )
            )
        )
    })
    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerResponseDTO> update(
            @Parameter(
                description = "Identificador único (UUID) da resposta a ser atualizada",
                required = true,
                example = "d1e2f3a4-b5c6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID answerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novo conteúdo da resposta",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AnswerCreateDTO.class),
                    examples = @ExampleObject(
                        name = "Exemplo de payload",
                        value = """
                            {
                              "answer": "Conteúdo corrigido: o evento inicia às 20h, não 19h."
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody AnswerCreateDTO answerUpdateDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        AnswerResponseDTO updatedAnswer = answerService.update(answerUpdateDTO, answerId, userDetails.getId());
        return ResponseEntity.ok(updatedAnswer);
    }

    // ─── DELETE /answers/{answerId} ──────────────────────────────────────────

    @Operation(
        summary = "Excluir resposta",
        description = "Remove permanentemente uma resposta. " +
                      "Apenas o autor original (identificado pelo JWT) pode excluí-la."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Resposta excluída com sucesso — sem conteúdo no corpo"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado — token JWT ausente ou inválido",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Resposta não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Answer not found with id: d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "description": "uri=/answers/d1e2f3a4-b5c6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 20:00:00"
                        }
                        """
                )
            )
        )
    })
    @DeleteMapping("/{answerId}")
    public ResponseEntity<?> delete(
            @Parameter(
                description = "Identificador único (UUID) da resposta a ser excluída",
                required = true,
                example = "d1e2f3a4-b5c6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID answerId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        answerService.deleteById(answerId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
