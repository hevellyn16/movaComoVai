package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.application.service.EventPictureService;
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
@RequestMapping("/event-pictures")
@RequiredArgsConstructor
@Tag(
    name = "Event Pictures",
    description = "Endpoints para gerenciamento de imagens vinculadas a eventos. " +
                  "O upload é realizado via `multipart/form-data`. " +
                  "Todas as operações requerem autenticação via JWT Bearer token."
)
@SecurityRequirement(name = "bearerAuth")
public class EventPictureController {

    private final EventPictureService eventPictureService;

    // ─── GET /event-pictures/{id} ─────────────────────────────────────────────

    @Operation(
        summary = "Buscar imagem de evento por ID",
        description = "Retorna os dados de uma imagem específica vinculada a um evento, " +
                      "com base no seu identificador único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Imagem encontrada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EventPictureResponseDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de imagem de evento",
                    value = """
                        {
                          "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "pictureUrl": "https://storage.example.com/events/banner_show.jpg"
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
                          "description": "uri=/event-pictures/f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "status": "UNAUTHORIZED",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Imagem não encontrada para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "EventPicture not found with id: f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "description": "uri=/event-pictures/f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventPictureResponseDTO> findById(
            @Parameter(
                description = "Identificador único (UUID) da imagem",
                required = true,
                example = "f1e2d3c4-b5a6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(eventPictureService.findById(id));
    }

    // ─── GET /event-pictures/events/{eventId} ─────────────────────────────────

    @Operation(
        summary = "Listar imagens de um evento",
        description = "Retorna de forma paginada todas as imagens vinculadas a um evento específico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Página de imagens retornada com sucesso (pode ser vazia)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Exemplo de página de imagens de evento",
                    value = """
                        {
                          "content": [
                            {
                              "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                              "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                              "pictureUrl": "https://storage.example.com/events/banner_show.jpg"
                            },
                            {
                              "id": "a2b3c4d5-e6f7-8901-bcde-f01234567891",
                              "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                              "pictureUrl": "https://storage.example.com/events/palco.jpg"
                            }
                          ],
                          "totalElements": 2,
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
            description = "Evento não encontrado para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Event not found with id: b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "description": "uri=/event-pictures/events/b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 15:30:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Page<EventPictureResponseDTO>> findByEventId(
            @Parameter(
                description = "Identificador único (UUID) do evento cujas imagens serão listadas",
                required = true,
                example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID eventId,
            @Parameter(hidden = true)
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(eventPictureService.findByEventId(eventId, pageable));
    }

    // ─── POST /event-pictures/{eventId} ───────────────────────────────────────

    @Operation(
        summary = "Fazer upload de imagem para um evento",
        description = "Realiza o upload de um arquivo de imagem e o vincula ao evento especificado. " +
                      "A requisição deve ser enviada como `multipart/form-data` com o campo `file` contendo o arquivo. " +
                      "O header `Location` da resposta conterá a URI da imagem criada."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Imagem enviada e vinculada com sucesso. O header `Location` contém a URI do novo recurso.",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = EventPictureResponseDTO.class),
                examples = @ExampleObject(
                    name = "Imagem criada",
                    value = """
                        {
                          "id": "f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "eventId": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "pictureUrl": "https://storage.example.com/events/banner_show.jpg"
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
                          "description": "uri=/event-pictures/b0e1d2c3-f4a5-9678-efab-cd1234567890",
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
            description = "Evento não encontrado para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Event not found with id: b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "description": "uri=/event-pictures/b0e1d2c3-f4a5-9678-efab-cd1234567890",
                          "status": "NOT_FOUND",
                          "timestamp": "2025-06-01 18:00:00"
                        }
                        """
                )
            )
        )
    })
    @PostMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventPictureResponseDTO> uploadPicture(
            @Parameter(
                description = "Identificador único (UUID) do evento ao qual a imagem será vinculada",
                required = true,
                example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
            )
            @PathVariable UUID eventId,
            @Parameter(
                description = "Arquivo de imagem a ser enviado (JPEG, PNG, etc.)",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        EventPictureResponseDTO createdPicture = eventPictureService.savePicture(eventId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPicture.id())
                .toUri();

        return ResponseEntity.created(location).body(createdPicture);
    }

    // ─── DELETE /event-pictures/{id} ──────────────────────────────────────────

    @Operation(
        summary = "Excluir imagem de um evento",
        description = "Remove permanentemente uma imagem vinculada a um evento com base no seu identificador único."
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
            description = "Imagem não encontrada para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "EventPicture not found with id: f1e2d3c4-b5a6-7890-abcd-ef1234567890",
                          "description": "uri=/event-pictures/f1e2d3c4-b5a6-7890-abcd-ef1234567890",
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
                description = "Identificador único (UUID) da imagem a ser excluída",
                required = true,
                example = "f1e2d3c4-b5a6-7890-abcd-ef1234567890"
            )
            @PathVariable UUID id) {
        eventPictureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
