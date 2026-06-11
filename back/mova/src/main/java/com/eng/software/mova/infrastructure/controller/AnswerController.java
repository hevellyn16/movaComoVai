package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.application.service.AnswerService;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> findById(@PathVariable UUID id) {
        AnswerResponseDTO answerResponseDTO = answerService.findById(id);
        return ResponseEntity.ok(answerResponseDTO);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Page<AnswerResponseDTO>> findByCommentId(@PathVariable UUID commentId) {
        Page<AnswerResponseDTO> answers = answerService.findByCommentId(commentId, Pageable.unpaged());
        return ResponseEntity.ok(answers);
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<AnswerResponseDTO> create(@PathVariable UUID commentId,
                                                    @Valid @RequestBody AnswerCreateDTO answerCreateDTO,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        AnswerResponseDTO createdAnswer = answerService.create(answerCreateDTO, userDetails.getId(), commentId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdAnswer.id())
                .toUri();

        return ResponseEntity.created(location).body(createdAnswer);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerResponseDTO> update(@PathVariable UUID answerId,
                                                    @Valid @RequestBody AnswerCreateDTO answerUpdateDTO,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        AnswerResponseDTO updatedAnswer = answerService.update(answerUpdateDTO, answerId, userDetails.getId());
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<?> delete(@PathVariable UUID answerId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        answerService.deleteById(answerId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
