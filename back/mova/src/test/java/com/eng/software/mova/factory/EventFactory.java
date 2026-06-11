package com.eng.software.mova.factory;

import com.eng.software.mova.application.dto.event.EventCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleUpdateDTO;
import com.eng.software.mova.application.dto.event.EventUpdateDTO;
import com.eng.software.mova.domain.model.*;
import com.eng.software.mova.domain.model.enums.UserType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Factory para criação de objetos de teste relacionados a Event.
 * Centraliza a construção de entidades de domínio e DTOs,
 * garantindo dados consistentes e reutilizáveis em toda a suíte de testes.
 */
public final class EventFactory {

    // ======================== IDs PADRÃO ========================

    public static final UUID DEFAULT_EVENT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final UUID DEFAULT_CREATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID DEFAULT_VENUE_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
    public static final UUID DEFAULT_PICTURE_ID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012");
    public static final UUID DEFAULT_SCHEDULE_ID = UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123");
    public static final UUID DEFAULT_TAG_ID_1 = UUID.fromString("e5f6a7b8-c9d0-1234-efab-345678901234");
    public static final UUID DEFAULT_TAG_ID_2 = UUID.fromString("f6a7b8c9-d0e1-2345-fabc-456789012345");

    // ======================== CONSTANTES ========================

    public static final String DEFAULT_EVENT_NAME = "Show de Rock no Parque";
    public static final String DEFAULT_DESCRIPTION = "Um grande show de rock ao ar livre com bandas locais e nacionais.";
    public static final String DEFAULT_CONTENT_RATING = "Livre";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("50.00");
    public static final LocalDateTime DEFAULT_STARTS_AT = LocalDateTime.of(2025, 8, 15, 19, 0, 0);
    public static final LocalDateTime DEFAULT_ENDS_AT = LocalDateTime.of(2025, 8, 15, 23, 0, 0);
    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.of(2025, 7, 1, 10, 0, 0);
    public static final LocalDateTime DEFAULT_UPDATED_AT = LocalDateTime.of(2025, 7, 10, 14, 0, 0);

    public static final String DEFAULT_PICTURE_URL = "http://localhost:8080/uploads/events/pic1.jpg";
    public static final String DEFAULT_SCHEDULE_TITLE = "Abertura com DJ Set";
    public static final String DEFAULT_SCHEDULE_DESCRIPTION = "DJ local tocando os melhores hits.";
    public static final LocalDateTime DEFAULT_SCHEDULE_TIME = LocalDateTime.of(2025, 8, 15, 19, 0, 0);

    private EventFactory() {
        // Utility class — impede instanciação
    }

    // ======================== USER (criador) ========================

    /**
     * Cria um User criador padrão para associar a eventos.
     */
    public static User createDefaultCreator() {
        return User.builder()
                .id(DEFAULT_CREATOR_ID)
                .name("João da Silva")
                .username("joaosilva")
                .email("joao@email.com")
                .password("$2a$10$encodedPasswordHash")
                .userType(UserType.COMMON)
                .createdAt(LocalDateTime.of(2025, 1, 15, 10, 30, 0))
                .updatedAt(LocalDateTime.of(2025, 6, 10, 14, 0, 0))
                .isActive(true)
                .isPrivate(false)
                .pushNotifications(true)
                .emailNotifications(true)
                .tagsId(new HashSet<>())
                .build();
    }

    // ======================== VENUE ========================

    /**
     * Cria um Venue padrão para associar a eventos.
     */
    public static Venue createDefaultVenue() {
        return Venue.builder()
                .id(DEFAULT_VENUE_ID)
                .name("Parque Ibirapuera")
                .number("s/n")
                .city("São Paulo")
                .street("Av. Pedro Álvares Cabral")
                .neighborhood("Vila Mariana")
                .landmark("Próximo ao MAM")
                .hasParkingLot(true)
                .hasAccessibility(true)
                .hasBathroom(true)
                .hasFoodsAndDrinks(true)
                .build();
    }

    // ======================== TAG ========================

    public static Tag createTag(UUID id, String name) {
        return Tag.builder().id(id).tagName(name).build();
    }

    public static Set<Tag> createDefaultTags() {
        return new HashSet<>(Set.of(
                createTag(DEFAULT_TAG_ID_1, "Rock"),
                createTag(DEFAULT_TAG_ID_2, "Ao Vivo")
        ));
    }

    // ======================== EVENT PICTURE ========================

    public static EventPicture createDefaultPicture() {
        return EventPicture.builder()
                .id(DEFAULT_PICTURE_ID)
                .pictureUrl(DEFAULT_PICTURE_URL)
                .build();
    }

    public static EventPicture createPicture(UUID id, String url) {
        return EventPicture.builder().id(id).pictureUrl(url).build();
    }

    // ======================== EVENT SCHEDULE ========================

    public static EventSchedule createDefaultSchedule() {
        return EventSchedule.builder()
                .id(DEFAULT_SCHEDULE_ID)
                .title(DEFAULT_SCHEDULE_TITLE)
                .description(DEFAULT_SCHEDULE_DESCRIPTION)
                .scheduleTime(DEFAULT_SCHEDULE_TIME)
                .build();
    }

    public static EventSchedule createSchedule(UUID id, String title) {
        return EventSchedule.builder()
                .id(id)
                .title(title)
                .description("Descrição de " + title)
                .scheduleTime(DEFAULT_SCHEDULE_TIME.plusHours(1))
                .build();
    }

    // ======================== EVENT DOMAIN ========================

    /**
     * Cria um Event de domínio completo com todos os relacionamentos.
     */
    public static Event createDefaultEvent() {
        return Event.builder()
                .id(DEFAULT_EVENT_ID)
                .user(createDefaultCreator())
                .venue(createDefaultVenue())
                .eventName(DEFAULT_EVENT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .contentRating(DEFAULT_CONTENT_RATING)
                .price(DEFAULT_PRICE)
                .startsAt(DEFAULT_STARTS_AT)
                .endsAt(DEFAULT_ENDS_AT)
                .createdAt(DEFAULT_CREATED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .tags(createDefaultTags())
                .pictures(new HashSet<>(Set.of(createDefaultPicture())))
                .schedules(new HashSet<>(Set.of(createDefaultSchedule())))
                .build();
    }

    /**
     * Cria um Event sem venue (evento sem local definido).
     */
    public static Event createEventWithoutVenue() {
        Event event = createDefaultEvent();
        event.setVenue(null);
        return event;
    }

    /**
     * Cria um Event sem tags.
     */
    public static Event createEventWithoutTags() {
        Event event = createDefaultEvent();
        event.setTags(null);
        return event;
    }

    /**
     * Cria um Event sem pictures.
     */
    public static Event createEventWithoutPictures() {
        Event event = createDefaultEvent();
        event.setPictures(null);
        return event;
    }

    /**
     * Cria um Event sem schedules.
     */
    public static Event createEventWithoutSchedules() {
        Event event = createDefaultEvent();
        event.setSchedules(null);
        return event;
    }

    /**
     * Cria um Event mínimo (sem venue, tags, pictures, schedules).
     */
    public static Event createMinimalEvent() {
        return Event.builder()
                .id(DEFAULT_EVENT_ID)
                .user(createDefaultCreator())
                .eventName(DEFAULT_EVENT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .contentRating(DEFAULT_CONTENT_RATING)
                .price(DEFAULT_PRICE)
                .startsAt(DEFAULT_STARTS_AT)
                .endsAt(DEFAULT_ENDS_AT)
                .createdAt(DEFAULT_CREATED_AT)
                .updatedAt(DEFAULT_UPDATED_AT)
                .build();
    }

    // ======================== CREATE DTO ========================

    /**
     * Cria um EventCreateDTO padrão válido com venue e tags.
     */
    public static EventCreateDTO createDefaultEventCreateDTO() {
        return new EventCreateDTO(
                DEFAULT_EVENT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_CONTENT_RATING,
                DEFAULT_PRICE,
                DEFAULT_STARTS_AT,
                DEFAULT_ENDS_AT,
                DEFAULT_VENUE_ID,
                Set.of(DEFAULT_TAG_ID_1, DEFAULT_TAG_ID_2)
        );
    }

    /**
     * Cria um EventCreateDTO sem venue e sem tags.
     */
    public static EventCreateDTO createMinimalEventCreateDTO() {
        return new EventCreateDTO(
                DEFAULT_EVENT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_CONTENT_RATING,
                DEFAULT_PRICE,
                DEFAULT_STARTS_AT,
                DEFAULT_ENDS_AT,
                null,
                null
        );
    }

    /**
     * Cria um EventCreateDTO com tags mas sem venue.
     */
    public static EventCreateDTO createEventCreateDTOWithTagsNoVenue() {
        return new EventCreateDTO(
                DEFAULT_EVENT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_CONTENT_RATING,
                DEFAULT_PRICE,
                DEFAULT_STARTS_AT,
                DEFAULT_ENDS_AT,
                null,
                Set.of(DEFAULT_TAG_ID_1)
        );
    }

    // ======================== UPDATE DTO ========================

    /**
     * Cria um EventUpdateDTO com todos os campos preenchidos.
     */
    public static EventUpdateDTO createFullEventUpdateDTO() {
        UUID newVenueId = UUID.randomUUID();
        return new EventUpdateDTO(
                "Show de Rock Atualizado",
                "Descrição atualizada do evento.",
                "12+",
                new BigDecimal("75.00"),
                DEFAULT_STARTS_AT.plusDays(30),
                DEFAULT_ENDS_AT.plusDays(30),
                newVenueId,
                Set.of(DEFAULT_TAG_ID_1)
        );
    }

    /**
     * Cria um EventUpdateDTO parcial — apenas nome.
     */
    public static EventUpdateDTO createNameOnlyEventUpdateDTO(String name) {
        return new EventUpdateDTO(name, null, null, null, null, null, null, null);
    }

    /**
     * Cria um EventUpdateDTO vazio (nenhum campo alterado).
     */
    public static EventUpdateDTO createEmptyEventUpdateDTO() {
        return new EventUpdateDTO(null, null, null, null, null, null, null, null);
    }

    /**
     * Cria um EventUpdateDTO com tags.
     */
    public static EventUpdateDTO createEventUpdateDTOWithTags(Set<UUID> tagIds) {
        return new EventUpdateDTO(null, null, null, null, null, null, null, tagIds);
    }

    /**
     * Cria um EventUpdateDTO com venue.
     */
    public static EventUpdateDTO createEventUpdateDTOWithVenue(UUID venueId) {
        return new EventUpdateDTO(null, null, null, null, null, null, venueId, null);
    }

    // ======================== SCHEDULE DTOs ========================

    /**
     * Cria um EventScheduleCreateDTO padrão.
     */
    public static EventScheduleCreateDTO createDefaultScheduleCreateDTO() {
        return new EventScheduleCreateDTO(
                DEFAULT_SCHEDULE_TITLE,
                DEFAULT_SCHEDULE_DESCRIPTION,
                DEFAULT_SCHEDULE_TIME
        );
    }

    /**
     * Cria um EventScheduleUpdateDTO com todos os campos.
     */
    public static EventScheduleUpdateDTO createFullScheduleUpdateDTO() {
        return new EventScheduleUpdateDTO(
                "Show Principal — Banda X",
                "Show principal da noite com a Banda X.",
                DEFAULT_SCHEDULE_TIME.plusHours(2)
        );
    }

    /**
     * Cria um EventScheduleUpdateDTO parcial — apenas título.
     */
    public static EventScheduleUpdateDTO createTitleOnlyScheduleUpdateDTO(String title) {
        return new EventScheduleUpdateDTO(title, null, null);
    }
}
