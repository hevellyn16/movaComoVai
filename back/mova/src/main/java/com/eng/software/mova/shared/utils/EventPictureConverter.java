package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import org.springframework.stereotype.Component;

@Component
public class EventPictureConverter {
    public static EventPicture entityToDomain(EventPictureEntity eventPictureEntity) {
        return EventPicture.builder()
                .id(eventPictureEntity.getId())
                .eventId(eventPictureEntity.getEvent().getId())
                .pictureUrl(eventPictureEntity.getPictureUrl())
                .build();
    }

    public static EventPictureEntity domainToEntity(EventPicture eventPicture) {
        return EventPictureEntity.builder()
                .id(eventPicture.getId())
                .pictureUrl(eventPicture.getPictureUrl())
                .event(EventEntity.builder().id(eventPicture.getEventId()).build())
                .build();
    }

    public static EventPictureResponseDTO domainToResponse(EventPicture eventPicture) {
        return EventPictureResponseDTO.builder()
                .id(eventPicture.getId())
                .eventId(eventPicture.getEventId())
                .pictureUrl(eventPicture.getPictureUrl())
                .build();
    }
}
