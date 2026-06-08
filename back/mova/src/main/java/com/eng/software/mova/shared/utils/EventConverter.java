package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.event.EventResponseDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import org.springframework.stereotype.Component;

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
                .pictures(entity.getPictures() != null ? entity.getPictures().stream()
                        .map(picEntity -> EventPicture.builder()
                                .id(picEntity.getId())
                                .pictureUrl(picEntity.getPictureUrl())
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

        return entity;
    }

    public static EventResponseDTO domainToResponse(Event domain) {
        if (domain == null) return null;
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
                        .map(Tag::getTagName).collect(Collectors.toSet()) : null
        );
    }
}
