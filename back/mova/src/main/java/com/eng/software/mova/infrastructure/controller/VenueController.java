package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.venue.VenueCreateDTO;
import com.eng.software.mova.application.dto.venue.VenueResponseDTO;
import com.eng.software.mova.application.dto.venue.VenueUpdateDTO;
import com.eng.software.mova.application.service.VenueService;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.shared.utils.VenueConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService service;

    @GetMapping
    public ResponseEntity<Page<VenueResponseDTO>> findAll(Pageable pageable) {
        Page<VenueResponseDTO> venues = service.findAll(pageable).map(VenueConverter::domainToResponse);
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> findById(@PathVariable UUID id) {
        Venue venue = service.findById(id);
        return ResponseEntity.ok(VenueConverter.domainToResponse(venue));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VenueResponseDTO>> searchVenues(
            @RequestParam(required = false) String q,
            @PageableDefault Pageable pageable) {

        Page<Venue> venues = service.search(q, pageable);
        return ResponseEntity.ok(venues.map(VenueConverter::domainToResponse));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> create(@RequestBody @Valid VenueCreateDTO dto) {
        Venue createdVenue = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(VenueConverter.domainToResponse(createdVenue));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid VenueUpdateDTO dto) {

        Venue updatedVenue = service.update(id, dto);
        return ResponseEntity.ok(VenueConverter.domainToResponse(updatedVenue));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
