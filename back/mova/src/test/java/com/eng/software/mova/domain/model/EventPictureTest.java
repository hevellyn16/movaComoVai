package com.eng.software.mova.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventPictureTest {

    @Test
    public void shouldBuildEventPictureWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPicture eventPicture = EventPicture.builder()
                .id(id)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        assertThat(eventPicture.getId()).isEqualTo(id);
        assertThat(eventPicture.getEventId()).isEqualTo(eventId);
        assertThat(eventPicture.getPictureUrl()).isEqualTo(url);
    }

    @Test
    public void shouldBuildEventPictureWithNullOptionalFields() {
        EventPicture eventPicture = EventPicture.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .build();

        assertThat(eventPicture.getPictureUrl()).isNull();
    }

    @Test
    public void shouldRespectEquality_whenSameId() {
        UUID id = UUID.randomUUID();

        EventPicture p1 = EventPicture.builder()
                .id(id)
                .eventId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        EventPicture p2 = EventPicture.builder()
                .id(id)
                .eventId(UUID.randomUUID())
                .pictureUrl("url-b")
                .build();

        assertThat(p1).isEqualTo(p2);
    }

    @Test
    public void shouldNotBeEqual_whenDifferentId() {
        EventPicture p1 = EventPicture.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        EventPicture p2 = EventPicture.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        assertThat(p1).isNotEqualTo(p2);
    }
}
