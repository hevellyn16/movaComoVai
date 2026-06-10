package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.venue.VenueCreateDTO;
import com.eng.software.mova.application.dto.venue.VenueResponseDTO;
import com.eng.software.mova.application.dto.venue.VenueUpdateDTO;
import com.eng.software.mova.application.service.VenueService;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.shared.utils.VenueConverter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
@Tag(name = "Locais (Venues)", description = "Gerenciamento de locais onde os eventos acontecem. "
        + "Inclui infraestrutura como estacionamento, acessibilidade e localização.")
public class VenueController {

    private final VenueService service;

    // ======================== ROTAS DE CONSULTA ========================

    @Operation(
            summary = "Listar todos os locais",
            description = "Retorna uma lista paginada de todos os locais cadastrados. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de locais retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<VenueResponseDTO>> findAll(Pageable pageable) {
        Page<VenueResponseDTO> venues = service.findAll(pageable).map(VenueConverter::domainToResponse);
        return ResponseEntity.ok(venues);
    }

    @Operation(
            summary = "Buscar local por ID",
            description = "Retorna os dados completos de um local específico pelo seu UUID. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Local encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> findById(
            @Parameter(description = "UUID do local (venue)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        Venue venue = service.findById(id);
        return ResponseEntity.ok(VenueConverter.domainToResponse(venue));
    }

    @Operation(
            summary = "Buscar locais com filtros avançados",
            description = "Busca locais com filtros opcionais por nome, cidade e bairro. "
                    + "**Acesso: usuário autenticado (COMMON ou ADMIN).**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<VenueResponseDTO>> search(
            @Parameter(description = "Nome parcial do local", example = "Parque")
            @RequestParam(required = false) String name,

            @Parameter(description = "Cidade", example = "São Paulo")
            @RequestParam(required = false) String city,

            @Parameter(description = "Bairro", example = "Pinheiros")
            @RequestParam(required = false) String neighborhood,

            Pageable pageable) {

        Page<VenueResponseDTO> venues = service.search(name, city, neighborhood, pageable)
                .map(VenueConverter::domainToResponse);
        return ResponseEntity.ok(venues);
    }

    // ======================== ROTAS DE GERENCIAMENTO (ADMIN) ========================

    @Operation(
            summary = "Criar novo local",
            description = "Cadastra um novo local (venue) com seu endereço e opções de infraestrutura. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Local criado com sucesso",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> create(@RequestBody @Valid VenueCreateDTO dto) {
        Venue createdVenue = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(VenueConverter.domainToResponse(createdVenue));
    }

    @Operation(
            summary = "Atualizar local",
            description = "Atualiza os dados de um local existente. Todos os campos são opcionais — envie apenas os que deseja alterar. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Local atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = VenueResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> update(
            @Parameter(description = "UUID do local a ser atualizado", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @RequestBody @Valid VenueUpdateDTO dto) {

        Venue updatedVenue = service.update(id, dto);
        return ResponseEntity.ok(VenueConverter.domainToResponse(updatedVenue));
    }

    @Operation(
            summary = "Excluir local",
            description = "Remove um local (venue) do sistema. O local não pode ser excluído se houver eventos associados a ele. "
                    + "**Acesso: somente ADMIN.**",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Local excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Local não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir o local pois existem eventos vinculados", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID do local a ser excluído", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
