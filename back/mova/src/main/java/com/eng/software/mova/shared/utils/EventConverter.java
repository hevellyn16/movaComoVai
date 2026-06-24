package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.event.EventResponseDTO;
import com.eng.software.mova.application.dto.event.EventScheduleResponseDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.model.EventSchedule;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import org.springframework.stereotype.Component;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventScheduleEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventConverter {

    public static Event entityToDomain(EventEntity entity) {
        if (entity == null) return null;
        return Event.builder()
                .id(entity.getId())
                .user(UserConverter.entityToDomain(entity.getUser()))
                .venue(VenueConverter.entityToDomain(entity.getVenue()))
                .eventName(entity.getEventName())
                .description(entity.getDescription())
                .contentRating(entity.getContentRating())
                .price(entity.getPrice())
                .startsAt(entity.getStartsAt())
                .endsAt(entity.getEndsAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .tags(entity.getTags() != null ? entity.getTags().stream()
                        .map(TagConverter::entityToDomain).collect(Collectors.toSet()) : null)
                .likedByUsers(entity.getLikedByUsers() != null ? entity.getLikedByUsers().stream()
                        .map(UserEntity::getId).collect(Collectors.toSet()) : null)
                .favoriteByUsers(entity.getFavoriteByUsers() != null ? entity.getFavoriteByUsers().stream()
                        .map(UserEntity::getId).collect(Collectors.toSet()) : null)
                .pictures(entity.getPictures() != null ? entity.getPictures().stream()
                        .map(picEntity -> EventPicture.builder()
                                .id(picEntity.getId())
                                .pictureUrl(picEntity.getPictureUrl())
                                .build())
                        .collect(Collectors.toSet()) : null)
                .schedules(entity.getSchedules() != null ? entity.getSchedules().stream()
                        .map(schedEntity -> EventSchedule.builder()
                                .id(schedEntity.getId())
                                .title(schedEntity.getTitle())
                                .description(schedEntity.getDescription())
                                .scheduleTime(schedEntity.getScheduleTime())
                                .build())
                        .collect(Collectors.toSet()) : null)
                .build();
    }

    public static EventEntity domainToEntity(Event domain) {
        if (domain == null) return null;

        EventEntity entity = EventEntity.builder()
                .id(domain.getId())
                .user(UserConverter.domainToEntity(domain.getUser()))
                .venue(VenueConverter.domainToEntity(domain.getVenue()))
                .eventName(domain.getEventName())
                .description(domain.getDescription())
                .contentRating(domain.getContentRating())
                .price(domain.getPrice())
                .startsAt(domain.getStartsAt())
                .endsAt(domain.getEndsAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .tags(domain.getTags() != null ? domain.getTags().stream()
                        .map(TagConverter::domainToEntity).collect(Collectors.toSet()) : null)
                .likedByUsers(domain.getLikedByUsers() != null ? domain.getLikedByUsers().stream()
                        .map(id -> UserEntity.builder().id(id).build()).collect(Collectors.toSet()) : null)
                .favoriteByUsers(domain.getFavoriteByUsers() != null ? domain.getFavoriteByUsers().stream()
                        .map(id -> UserEntity.builder().id(id).build()).collect(Collectors.toSet()) : null)
                .build();

        if (domain.getPictures() != null) {
            Set<EventPictureEntity> pictureEntities = domain.getPictures().stream().map(pic -> {
                EventPictureEntity picEntity = new EventPictureEntity();
                picEntity.setId(pic.getId());
                picEntity.setPictureUrl(pic.getPictureUrl());
                picEntity.setEvent(entity);
                return picEntity;
            }).collect(Collectors.toSet());

            entity.setPictures(pictureEntities);
        }

        if (domain.getSchedules() != null) {
            Set<EventScheduleEntity> scheduleEntities = domain.getSchedules().stream().map(sched -> {
                EventScheduleEntity schedEntity = new EventScheduleEntity();
                schedEntity.setId(sched.getId());
                schedEntity.setTitle(sched.getTitle());
                schedEntity.setDescription(sched.getDescription());
                schedEntity.setScheduleTime(sched.getScheduleTime());
                schedEntity.setEvent(entity);
                return schedEntity;
            }).collect(Collectors.toSet());

            entity.setSchedules(scheduleEntities);
        }

        return entity;
    }

    public static EventResponseDTO domainToResponse(Event domain) {
        if (domain == null) return null;

        List<EventScheduleResponseDTO> schedules = domain.getSchedules() != null ?
                domain.getSchedules().stream()
                        .map(s -> new EventScheduleResponseDTO(s.getId(), s.getTitle(), s.getDescription(), s.getScheduleTime()))
                        .collect(Collectors.toList()) : null;

        return new EventResponseDTO(
                domain.getId(),
                domain.getEventName(),
                domain.getDescription(),
                domain.getContentRating(),
                domain.getPrice(),
                domain.getStartsAt(),
                domain.getEndsAt(),
                domain.getUser() != null ? domain.getUser().getId() : null,
                domain.getVenue() != null ? domain.getVenue().getId() : null,
                domain.getVenue() != null ? domain.getVenue().getName() : null,
                domain.getTags() != null ? domain.getTags().stream()
                        .map(Tag::getTagName).collect(Collectors.toSet()) : null,
                schedules,
                domain.getLikedByUsers() != null ? domain.getLikedByUsers() : null,
                domain.getFavoriteByUsers() != null ? domain.getFavoriteByUsers() : null
        );
    }
}
