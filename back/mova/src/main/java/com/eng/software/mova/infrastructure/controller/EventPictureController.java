package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.application.service.EventPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/event-pictures")
@RequiredArgsConstructor
public class EventPictureController {
    private final EventPictureService eventPictureService;

    @GetMapping("/{id}")
    public ResponseEntity<EventPictureResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventPictureService.findById(id));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Set<EventPictureResponseDTO>> findByEventId(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventPictureService.findByEventId(eventId));
    }

    @PostMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventPictureResponseDTO> uploadPicture(
            @PathVariable UUID eventId,
            @RequestParam("file") MultipartFile file) {

        EventPictureResponseDTO createdPicture = eventPictureService.savePicture(eventId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPicture.id())
                .toUri();

        return ResponseEntity.created(location).body(createdPicture);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        eventPictureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
