package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventPictureConverterTest {

    @Test
    public void shouldMapEntityToDomain_whenEntityIsValid() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPictureEntity entity = EventPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .event(EventEntity.builder().id(eventId).build())
                .build();

        EventPicture domain = EventPictureConverter.entityToDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getEventId()).isEqualTo(eventId);
        assertThat(domain.getPictureUrl()).isEqualTo(url);
    }

    @Test
    public void shouldMapDomainToEntity_whenDomainIsValid() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPicture domain = EventPicture.builder()
                .id(id)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        EventPictureEntity entity = EventPictureConverter.domainToEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getPictureUrl()).isEqualTo(url);
        assertThat(entity.getEvent()).isNotNull();
        assertThat(entity.getEvent().getId()).isEqualTo(eventId);
    }

    @Test
    public void shouldMapDomainToResponse_whenDomainIsValid() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPicture domain = EventPicture.builder()
                .id(id)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        EventPictureResponseDTO response = EventPictureConverter.domainToResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.eventId()).isEqualTo(eventId);
        assertThat(response.pictureUrl()).isEqualTo(url);
    }
}
