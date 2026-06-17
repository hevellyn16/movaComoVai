package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.event.EventResponseDTO;
import com.eng.software.mova.application.service.RecommendationService;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import com.eng.software.mova.shared.utils.EventConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(
    name = "Recommendations",
    description = "Endpoints para recomendação personalizada de eventos. " +
                  "O feed é montado com base nas preferências e interações do usuário autenticado. " +
                  "Todas as operações requerem autenticação via JWT Bearer token."
)
@SecurityRequirement(name = "bearerAuth")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // ─── GET /recommendations/feed ───────────────────────────────────────────

    @Operation(
        summary = "Obter feed personalizado de eventos",
        description = "Retorna uma lista paginada de eventos recomendados para o usuário autenticado, " +
                      "com base nas suas tags de interesse, histórico de interações e localização. " +
                      "O usuário é identificado automaticamente pelo token JWT informado no header."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Feed de eventos retornado com sucesso (pode ser vazio caso não haja recomendações)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Exemplo de feed paginado",
                    value = """
                        {
                          "content": [
                            {
                              "id": "b0e1d2c3-f4a5-9678-efab-cd1234567890",
                              "eventName": "Festival de Jazz na Praça",
                              "description": "Uma noite incrível de jazz ao ar livre.",
                              "contentRating": "livre",
                              "price": 0.00,
                              "startsAt": "2025-07-10T19:00:00",
                              "endsAt": "2025-07-10T23:00:00",
                              "creatorId": "a1b2c3d4-e5f6-7890-abcd-111122223333",
                              "venueId": "c3d4e5f6-a7b8-9012-cdef-123456789abc",
                              "venueName": "Praça Central",
                              "tags": ["música", "jazz", "gratuito"],
                              "likedByUserIds": []
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
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    value = """
                        {
                          "error": "Unauthorized",
                          "description": "uri=/recommendations/feed",
                          "status": "UNAUTHORIZED",
                          "timestamp": "2025-07-01 10:00:00"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/feed")
    public ResponseEntity<Page<EventResponseDTO>> getFeed(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(hidden = true) Pageable pageable) {

        Page<Event> feed = recommendationService.getFeedForUser(userDetails.getId(), pageable);

        return ResponseEntity.ok(feed.map(EventConverter::domainToResponse));
    }
}