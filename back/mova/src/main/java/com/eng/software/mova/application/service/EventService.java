package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.event.EventCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleUpdateDTO;
import com.eng.software.mova.application.dto.event.EventUpdateDTO;
import com.eng.software.mova.domain.model.*;
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
import java.util.HashSet;
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

    @Transactional
    public void likeEvent(UUID eventId, UUID id) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        event.getLikedByUsers().add(user.getId());
        eventRepositoryPort.save(event);
    }

    @Transactional
    public void unlikeEvent(UUID eventId, UUID id) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        event.getLikedByUsers().remove(user.getId());
        eventRepositoryPort.save(event);
    }

    @Transactional
    public void addFavorite(UUID eventId, UUID id) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (event.getFavoriteByUsers() == null) {
            event.setFavoriteByUsers(java.util.Collections.singleton(user.getId()));
        } else {
            event.getFavoriteByUsers().add(user.getId());
        }

        // keep user's favorites in sync on domain side
        if (user.getFavoriteEvents() == null) {
            user.setFavoriteEvents(java.util.Collections.singleton(event.getId()));
        } else {
            user.getFavoriteEvents().add(event.getId());
        }

        eventRepositoryPort.save(event);
    }

    @Transactional
    public void removeFavorite(UUID eventId, UUID id) {
        Event event = eventRepositoryPort.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (event.getFavoriteByUsers() != null) {
            event.getFavoriteByUsers().remove(user.getId());
        }

        if (user.getFavoriteEvents() != null) {
            user.getFavoriteEvents().remove(event.getId());
        }

        eventRepositoryPort.save(event);
    }

    @Transactional
    public void addTagsToEvent(UUID eventId, Set<UUID> tagIds) {
        Event event = findById(eventId);

        Set<Tag> tagsToAdd = tagService.getTagsAndVerify(tagIds);

        if (event.getTags() == null) {
            event.setTags(new HashSet<>());
        }
        event.getTags().addAll(tagsToAdd);

        event.setUpdatedAt(LocalDateTime.now());

        eventRepositoryPort.save(event);
    }

    @Transactional
    public void removeTagFromEvent(UUID eventId, UUID tagId) {
        Event event = findById(eventId);

        if (event.getTags() != null) {
            boolean removed = event.getTags().removeIf(tag -> tag.getId().equals(tagId));

            if (removed) {
                event.setUpdatedAt(LocalDateTime.now());
                eventRepositoryPort.save(event);
            }
        }
    }

    @Transactional
    public void addPictureToEvent(UUID eventId, String pictureUrl) {
        Event event = findById(eventId);

        if (event.getPictures() == null) {
            event.setPictures(new java.util.HashSet<>());
        }

        EventPicture newPicture = EventPicture.builder()
                .pictureUrl(pictureUrl)
                .build();

        event.getPictures().add(newPicture);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepositoryPort.save(event);
    }

    @Transactional
    public void removePictureFromEvent(UUID eventId, UUID pictureId) {
        Event event = findById(eventId);

        boolean hasPicture = event.getPictures() != null &&
                event.getPictures().stream().anyMatch(pic -> pic.getId().equals(pictureId));

        if (hasPicture) {
            eventRepositoryPort.deletePictureById(pictureId);
        } else {
            throw new ResourceNotFoundException("Picture not found for this event.");
        }
    }

    @Transactional
    public void addScheduleToEvent(UUID eventId, EventScheduleCreateDTO dto) {
        Event event = findById(eventId);

        if (event.getSchedules() == null) {
            event.setSchedules(new HashSet<>());
        }

        EventSchedule newSchedule = EventSchedule.builder()
                .title(dto.title())
                .description(dto.description())
                .scheduleTime(dto.scheduleTime())
                .build();

        event.getSchedules().add(newSchedule);
        event.setUpdatedAt(LocalDateTime.now());

        eventRepositoryPort.save(event);
    }

    @Transactional
    public void updateSchedule(UUID eventId, UUID scheduleId, EventScheduleUpdateDTO dto) {
        Event event = findById(eventId);

        EventSchedule schedule = event.getSchedules() != null ?
                event.getSchedules().stream()
                        .filter(s -> s.getId().equals(scheduleId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Programação não encontrada para este evento."))
                : null;

        if (schedule == null) {
            throw new ResourceNotFoundException("Programação não encontrada para este evento.");
        }

        if (dto.title() != null) schedule.setTitle(dto.title());
        if (dto.description() != null) schedule.setDescription(dto.description());
        if (dto.scheduleTime() != null) schedule.setScheduleTime(dto.scheduleTime());

        event.setUpdatedAt(LocalDateTime.now());
        eventRepositoryPort.save(event);
    }

    @Transactional
    public void removeScheduleFromEvent(UUID eventId, UUID scheduleId) {
        Event event = findById(eventId);

        boolean hasSchedule = event.getSchedules() != null &&
                event.getSchedules().stream().anyMatch(s -> s.getId().equals(scheduleId));

        if (hasSchedule) {
            eventRepositoryPort.deleteScheduleById(scheduleId);
        } else {
            throw new ResourceNotFoundException("Programação não encontrada para este evento.");
        }
    }
}