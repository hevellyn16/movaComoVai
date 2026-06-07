package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.application.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> findAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(tagService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDTO> create(@Valid @RequestBody TagCreateDTO dto) {
        TagResponseDTO createdTag = tagService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTag.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTag);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TagUpdateDTO dto) {
        return ResponseEntity.ok(tagService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
