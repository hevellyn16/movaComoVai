package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.event.*;
import com.eng.software.mova.application.service.EventService;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import com.eng.software.mova.shared.utils.EventConverter;
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
public class EventController {

    private final EventService service;

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> findAll(Pageable pageable) {
        Page<EventResponseDTO> events = service.findAll(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(EventConverter.domainToResponse(service.findById(id)));
    }

    @GetMapping("/today")
    public ResponseEntity<Page<EventResponseDTO>> findToday(Pageable pageable) {
        Page<EventResponseDTO> events = service.findTodayEvents(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponseDTO>> findUpcoming(Pageable pageable) {
        Page<EventResponseDTO> events = service.findUpcomingEvents(pageable).map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDTO>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String neighborhood,
            Pageable pageable) {

        Page<EventResponseDTO> events = service.search(q, dateFrom, dateTo, priceMin, priceMax, neighborhood, pageable)
                .map(EventConverter::domainToResponse);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> create(
            @RequestBody @Valid EventCreateDTO dto,
            @RequestAttribute String userId) {

        Event createdEvent = service.create(dto, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(EventConverter.domainToResponse(createdEvent));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid EventUpdateDTO dto) {
        Event updatedEvent = service.update(id, dto);
        return ResponseEntity.ok(EventConverter.domainToResponse(updatedEvent));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addTagsToEvent(
            @PathVariable UUID eventId,
            @RequestBody @Valid EventTagAssociationDTO dto) {

        service.addTagsToEvent(eventId, dto.tagIds());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/tags/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeTagFromEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID tagId) {

        service.removeTagFromEvent(eventId, tagId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/pictures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addPictureToEvent(
            @PathVariable UUID eventId,
            @RequestBody @Valid EventPictureCreateDTO dto) {

        service.addPictureToEvent(eventId, dto.pictureUrl());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/pictures/{pictureId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removePictureFromEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID pictureId) {

        service.removePictureFromEvent(eventId, pictureId);
        return ResponseEntity.noContent().build();
    }
}
