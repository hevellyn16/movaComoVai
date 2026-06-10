package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.application.service.CommentService;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> findById(@PathVariable UUID id) {
        CommentResponseDTO comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Page<CommentResponseDTO>> findByEventId(@PathVariable UUID eventId,
                                                                  @PageableDefault Pageable pageable) {
        Page<CommentResponseDTO> comments = commentService.findAllByEventId(eventId, pageable);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/events/{eventId}")
    public ResponseEntity<CommentResponseDTO> create(@PathVariable UUID eventId,
                                                     @Valid @RequestBody CommentCreateDTO createDTO,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponseDTO comment = commentService.create(createDTO, userDetails.getId(), eventId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comment.id())
                .toUri();

        return ResponseEntity.created(location).body(comment);
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Void> likeComment(@PathVariable UUID commentId, @AuthenticationPrincipal CustomUserDetails user) {
        commentService.likeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable UUID commentId,
                                                     @Valid @RequestBody CommentCreateDTO commentDTO,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponseDTO updatedComment = commentService.update(commentId, commentDTO, userDetails.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<Void> unlikeComment(@PathVariable UUID commentId, @AuthenticationPrincipal CustomUserDetails user) {
        commentService.unlikeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
}
