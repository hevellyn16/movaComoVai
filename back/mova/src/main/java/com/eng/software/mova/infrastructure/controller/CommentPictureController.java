package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.application.service.CommentPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/comments/{commentId}/pictures")
@RequiredArgsConstructor
public class CommentPictureController {
    private final CommentPictureService commentPictureService;

    @GetMapping("/{id}")
    public ResponseEntity<CommentPictureResponseDTO> findById(@PathVariable UUID commentId, @PathVariable UUID id) {
        return ResponseEntity.ok(commentPictureService.findById(commentId, id));
    }

    @GetMapping
    public ResponseEntity<Set<CommentPictureResponseDTO>> findByCommentId(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentPictureService.findByCommentId(commentId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentPictureResponseDTO> uploadPicture(
            @PathVariable UUID commentId,
            @RequestParam("file") MultipartFile file) {

        CommentPictureResponseDTO createdPicture = commentPictureService.savePicture(commentId, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPicture.id())
                .toUri();

        return ResponseEntity.created(location).body(createdPicture);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID commentId, @PathVariable UUID id) {
        commentPictureService.deleteById(commentId, id);
        return ResponseEntity.noContent().build();
    }
}



