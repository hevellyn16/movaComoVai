package com.eng.software.mova.application.service;

import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.RecommendationEngine;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepositoryPort userRepository;
    private final EventRepositoryPort eventRepository;


    public Page<Event> getFeedForUser(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Event> upcomingEvents = eventRepository.findAllUpcomingAsList();

        List<Event> recommendedFeed = RecommendationEngine.generateFeed(user, upcomingEvents);


        return convertListToPage(recommendedFeed, pageable);
    }

    private Page<Event> convertListToPage(List<Event> list, Pageable pageable) {
        int start = (int) pageable.getOffset();

        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) return new PageImpl<>(Collections.emptyList(), pageable, list.size());

        List<Event> subList = list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }
}
