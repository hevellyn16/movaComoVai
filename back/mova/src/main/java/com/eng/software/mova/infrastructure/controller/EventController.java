package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.event.*;
import com.eng.software.mova.application.service.EventService;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import com.eng.software.mova.shared.utils.EventConverter;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Gerenciamento de eventos: criação, consulta, busca avançada, tags, imagens e programação.")
public class EventController {

    private final EventService service;

    // ======================== ROTAS DE CONSULTA (USUÁRIO AUTENTICADO) ========================

    @Operation(
            summary = "Listar todos os eventos",
            description = "Retorna uma lista paginada de todos os eventos cadastrados, com suporte a ordenação. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> findAll(Pageable pageable) {
        Page<EventResponseDTO> events = service.findAll(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Buscar evento por ID",
            description = "Retorna os dados completos de um evento específico, incluindo tags, programação e local. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> findById(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(EventConverter.domainToResponse(service.findById(id)));
    }

    @Operation(
            summary = "Listar eventos do dia",
            description = "Retorna uma lista paginada dos eventos que acontecem hoje (entre 00:00 e 23:59 do dia atual). "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eventos do dia retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping("/today")
    public ResponseEntity<Page<EventResponseDTO>> findToday(Pageable pageable) {
        Page<EventResponseDTO> events = service.findTodayEvents(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Listar eventos futuros",
            description = "Retorna uma lista paginada dos eventos que ainda não começaram (data de início posterior a agora). "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eventos futuros retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponseDTO>> findUpcoming(Pageable pageable) {
        Page<EventResponseDTO> events = service.findUpcomingEvents(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Buscar eventos com filtros avançados",
            description = "Busca textual livre e filtros avançados por data, faixa de preço e bairro. "
                    + "Todos os parâmetros de filtro são opcionais e combinados com AND. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDTO>> search(
            @Parameter(description = "Texto para busca livre (nome, descrição)", example = "show de rock")
            @RequestParam(required = false) String q,

            @Parameter(description = "Data/hora mínima (formato ISO 8601)", example = "2025-07-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,

            @Parameter(description = "Data/hora máxima (formato ISO 8601)", example = "2025-07-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,

            @Parameter(description = "Preço mínimo do ingresso", example = "0.00")
            @RequestParam(required = false) BigDecimal priceMin,

            @Parameter(description = "Preço máximo do ingresso", example = "100.00")
            @RequestParam(required = false) BigDecimal priceMax,

            @Parameter(description = "Bairro do local do evento", example = "Pinheiros")
            @RequestParam(required = false) String neighborhood,

            Pageable pageable) {

        Page<EventResponseDTO> events = service.search(q, dateFrom, dateTo, priceMin, priceMax, neighborhood, pageable)
                .map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    // ======================== ROTAS DE GERENCIAMENTO (ADMIN) ========================

    @Operation(
            summary = "Criar novo evento",
            description = "Cadastra um novo evento com todos os campos obrigatórios. "
                    + "O evento é associado ao usuário autenticado como criador. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (validação falhou)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Venue não encontrado", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> create(
            @RequestBody @Valid EventCreateDTO dto,
            @Parameter(hidden = true) @RequestAttribute String userId) {

        Event createdEvent = service.create(dto, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(EventConverter.domainToResponse(createdEvent));
    }

    @Operation(
            summary = "Atualizar evento",
            description = "Atualiza os dados de um evento existente. Todos os campos são opcionais — envie apenas os que deseja alterar. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = EventResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> update(
            @Parameter(description = "UUID do evento a ser atualizado", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody @Valid EventUpdateDTO dto) {
        Event updatedEvent = service.update(id, dto);
        return ResponseEntity.ok(EventConverter.domainToResponse(updatedEvent));
    }

    @Operation(
            summary = "Excluir evento",
            description = "Remove um evento do sistema permanentemente. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID do evento a ser excluído", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ======================== TAGS DO EVENTO (ADMIN) ========================

    @Operation(
            summary = "Associar tags a um evento",
            description = "Adiciona uma ou mais tags a um evento existente. As tags devem existir previamente no sistema. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tags associadas ao evento com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de tags vazia ou inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento ou tag(s) não encontrado(s)", content = @Content)
    })
    @PostMapping("/{eventId}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addTagsToEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @RequestBody @Valid EventTagAssociationDTO dto) {

        service.addTagsToEvent(eventId, dto.tagIds());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover tag de um evento",
            description = "Remove a associação de uma tag específica com um evento. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag removida do evento com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento ou tag não encontrado(s)", content = @Content)
    })
    @DeleteMapping("/{eventId}/tags/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeTagFromEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @Parameter(description = "UUID da tag a ser removida", example = "660e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID tagId) {

        service.removeTagFromEvent(eventId, tagId);
        return ResponseEntity.noContent().build();
    }

    // ======================== IMAGENS DO EVENTO (ADMIN) ========================

    @Operation(
            summary = "Adicionar imagem ao evento",
            description = "Adiciona uma URL de imagem ao evento especificado. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Imagem adicionada ao evento com sucesso"),
            @ApiResponse(responseCode = "400", description = "URL da imagem vazia ou inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @PostMapping("/{eventId}/pictures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addPictureToEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @RequestBody @Valid EventPictureCreateDTO dto) {

        service.addPictureToEvent(eventId, dto.pictureUrl());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover imagem do evento",
            description = "Remove uma imagem específica de um evento pelo ID da imagem. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Imagem removida do evento com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento ou imagem não encontrado(s)", content = @Content)
    })
    @DeleteMapping("/{eventId}/pictures/{pictureId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removePictureFromEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @Parameter(description = "UUID da imagem a ser removida", example = "770e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID pictureId) {

        service.removePictureFromEvent(eventId, pictureId);
        return ResponseEntity.noContent().build();
    }

    // ======================== PROGRAMAÇÃO DO EVENTO (ADMIN) ========================

    @Operation(
            summary = "Adicionar item à programação do evento",
            description = "Adiciona um novo horário/atividade à programação (schedule) de um evento. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item da programação adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado", content = @Content)
    })
    @PostMapping("/{eventId}/schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addScheduleToEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @RequestBody @Valid EventScheduleCreateDTO dto) {

        service.addScheduleToEvent(eventId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Atualizar item da programação",
            description = "Atualiza o título, descrição ou horário de um item da programação de um evento. "
                    + "Campos opcionais — envie apenas os que deseja alterar. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item da programação atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento ou item da programação não encontrado", content = @Content)
    })
    @PutMapping("/{eventId}/schedules/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateSchedule(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @Parameter(description = "UUID do item da programação", example = "880e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID scheduleId,
            @RequestBody @Valid EventScheduleUpdateDTO dto) {

        service.updateSchedule(eventId, scheduleId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover item da programação",
            description = "Remove um item da programação de um evento. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item da programação removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento ou item da programação não encontrado", content = @Content)
    })
    @DeleteMapping("/{eventId}/schedules/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeScheduleFromEvent(
            @Parameter(description = "UUID do evento", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID eventId,
            @Parameter(description = "UUID do item da programação", example = "880e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID scheduleId) {

        service.removeScheduleFromEvent(eventId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
