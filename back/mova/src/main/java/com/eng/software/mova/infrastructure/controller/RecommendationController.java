package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.event.EventResponseDTO;
import com.eng.software.mova.application.service.RecommendationService;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import com.eng.software.mova.shared.utils.EventConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/feed")
    public ResponseEntity<Page<EventResponseDTO>> getFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {

        Page<Event> feed = recommendationService.getFeedForUser(userDetails.getId(), pageable);

        return ResponseEntity.ok(feed.map(EventConverter::domainToResponse));
    }
}