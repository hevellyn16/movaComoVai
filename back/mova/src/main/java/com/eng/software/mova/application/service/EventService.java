package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.event.EventCreateDTO;
import com.eng.software.mova.application.dto.event.EventUpdateDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepositoryPort eventRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final VenueService venueService;
    private final TagService tagService;

    @Transactional
    public Event create(EventCreateDTO dto, UUID creatorId) {
        Set<Tag> tags = null;
        if (dto.tagIds() != null && !dto.tagIds().isEmpty()) {
            tags = tagService.getTagsAndVerify(dto.tagIds());
        }

        User creator = userRepositoryPort.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Venue venue = null;
        if (dto.venueId() != null) {
            venue = venueService.findById(dto.venueId());
        }

        Event event = Event.builder()
                .user(creator)
                .venue(venue)
                .eventName(dto.eventName())
                .description(dto.description())
                .contentRating(dto.contentRating())
                .price(dto.price())
                .startsAt(dto.startsAt())
                .endsAt(dto.endsAt())
                .tags(tags)
                .build();

        return eventRepositoryPort.save(event);
    }

    @Transactional(readOnly = true)
    public Page<Event> findAll(Pageable pageable) {
        return eventRepositoryPort.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Event findById(UUID id) {
        return eventRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));
    }

    @Transactional(readOnly = true)
    public Page<Event> findTodayEvents(Pageable pageable) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return eventRepositoryPort.findTodayEvents(startOfDay, endOfDay, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Event> findUpcomingEvents(Pageable pageable) {
        return eventRepositoryPort.findUpcomingEvents(LocalDateTime.now(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Event> search(String q, LocalDateTime dateFrom, LocalDateTime dateTo, BigDecimal priceMin, BigDecimal priceMax, String neighborhood, Pageable pageable) {
        return eventRepositoryPort.search(q, dateFrom, dateTo, priceMin, priceMax, neighborhood, pageable);
    }

    @Transactional
    public Event update(UUID id, EventUpdateDTO dto) {
        Event existingEvent = findById(id);

        if (dto.eventName() != null) existingEvent.setEventName(dto.eventName());
        if (dto.description() != null) existingEvent.setDescription(dto.description());
        if (dto.contentRating() != null) existingEvent.setContentRating(dto.contentRating());
        if (dto.price() != null) existingEvent.setPrice(dto.price());
        if (dto.startsAt() != null) existingEvent.setStartsAt(dto.startsAt());
        if (dto.endsAt() != null) existingEvent.setEndsAt(dto.endsAt());

        if (dto.venueId() != null) {
            existingEvent.setVenue(venueService.findById(dto.venueId()));
        }

        if (dto.tagIds() != null) {
            tagService.verifyAllTagsExist(dto.tagIds());
            Set<Tag> newTags = dto.tagIds().stream()
                    .map(tagId -> Tag.builder().id(tagId).build()).collect(Collectors.toSet());
            existingEvent.setTags(newTags);
        }

        return eventRepositoryPort.save(existingEvent);
    }

    @Transactional
    public void delete(UUID id) {
        findById(id);
        eventRepositoryPort.deleteById(id);
    }
}