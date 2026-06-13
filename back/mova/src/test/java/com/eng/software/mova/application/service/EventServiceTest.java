package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.event.EventCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleCreateDTO;
import com.eng.software.mova.application.dto.event.EventScheduleUpdateDTO;
import com.eng.software.mova.application.dto.event.EventUpdateDTO;
import com.eng.software.mova.domain.model.*;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.factory.EventFactory;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários do {@link EventService}.
 * <p>
 * Utiliza Mockito com BDD style (given/when/then) e AssertJ para asserções fluidas.
 * Cada método público do service possui um {@link Nested} group próprio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService — Testes Unitários")
class EventServiceTest {

    @Mock
    private EventRepositoryPort eventRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private VenueService venueService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    // ======================== create ========================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar evento com venue e tags com sucesso")
        void shouldCreateEventWithVenueAndTags() {
            // given
            EventCreateDTO dto = EventFactory.createDefaultEventCreateDTO();
            User creator = EventFactory.createDefaultCreator();
            Venue venue = EventFactory.createDefaultVenue();
            Set<Tag> tags = EventFactory.createDefaultTags();

            given(tagService.getTagsAndVerify(dto.tagIds())).willReturn(tags);
            given(userRepositoryPort.findById(EventFactory.DEFAULT_CREATOR_ID)).willReturn(Optional.of(creator));
            given(venueService.findById(dto.venueId())).willReturn(venue);
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> {
                Event e = inv.getArgument(0);
                e.setId(EventFactory.DEFAULT_EVENT_ID);
                return e;
            });

            // when
            Event result = eventService.create(dto, EventFactory.DEFAULT_CREATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEventName()).isEqualTo(EventFactory.DEFAULT_EVENT_NAME);
            assertThat(result.getDescription()).isEqualTo(EventFactory.DEFAULT_DESCRIPTION);
            assertThat(result.getContentRating()).isEqualTo(EventFactory.DEFAULT_CONTENT_RATING);
            assertThat(result.getPrice()).isEqualByComparingTo(EventFactory.DEFAULT_PRICE);
            assertThat(result.getUser()).isEqualTo(creator);
            assertThat(result.getVenue()).isEqualTo(venue);
            assertThat(result.getTags()).isEqualTo(tags);

            then(tagService).should().getTagsAndVerify(dto.tagIds());
            then(userRepositoryPort).should().findById(EventFactory.DEFAULT_CREATOR_ID);
            then(venueService).should().findById(dto.venueId());
            then(eventRepositoryPort).should().save(any(Event.class));
        }

        @Test
        @DisplayName("Deve criar evento sem venue e sem tags")
        void shouldCreateEventWithoutVenueAndTags() {
            // given
            EventCreateDTO dto = EventFactory.createMinimalEventCreateDTO();
            User creator = EventFactory.createDefaultCreator();

            given(userRepositoryPort.findById(EventFactory.DEFAULT_CREATOR_ID)).willReturn(Optional.of(creator));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.create(dto, EventFactory.DEFAULT_CREATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getVenue()).isNull();
            assertThat(result.getTags()).isNull();

            then(tagService).shouldHaveNoInteractions();
            then(venueService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve criar evento com tags mas sem venue")
        void shouldCreateEventWithTagsButNoVenue() {
            // given
            EventCreateDTO dto = EventFactory.createEventCreateDTOWithTagsNoVenue();
            User creator = EventFactory.createDefaultCreator();
            Set<Tag> tags = Set.of(EventFactory.createTag(EventFactory.DEFAULT_TAG_ID_1, "Rock"));

            given(tagService.getTagsAndVerify(dto.tagIds())).willReturn(tags);
            given(userRepositoryPort.findById(EventFactory.DEFAULT_CREATOR_ID)).willReturn(Optional.of(creator));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.create(dto, EventFactory.DEFAULT_CREATOR_ID);

            // then
            assertThat(result.getVenue()).isNull();
            assertThat(result.getTags()).hasSize(1);

            then(venueService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o criador não existe")
        void shouldThrowExceptionWhenCreatorNotFound() {
            // given
            EventCreateDTO dto = EventFactory.createMinimalEventCreateDTO();
            UUID creatorId = UUID.randomUUID();

            given(userRepositoryPort.findById(creatorId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.create(dto, creatorId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado");

            then(eventRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== findById ========================

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve retornar o evento quando ele existe")
        void shouldReturnEventWhenExists() {
            // given
            Event event = EventFactory.createDefaultEvent();
            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            Event result = eventService.findById(EventFactory.DEFAULT_EVENT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(EventFactory.DEFAULT_EVENT_ID);
            assertThat(result.getEventName()).isEqualTo(EventFactory.DEFAULT_EVENT_NAME);
            then(eventRepositoryPort).should().findById(EventFactory.DEFAULT_EVENT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Evento não encontrado");
        }
    }

    // ======================== findAll ========================

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Deve retornar página de eventos")
        void shouldReturnPageOfEvents() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Event event1 = EventFactory.createDefaultEvent();
            Event event2 = EventFactory.createMinimalEvent();
            event2.setId(UUID.randomUUID());
            event2.setEventName("Festival de Jazz");
            Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), pageable, 2);

            given(eventRepositoryPort.findAll(pageable)).willReturn(eventPage);

            // when
            Page<Event> result = eventService.findAll(pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            then(eventRepositoryPort).should().findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há eventos")
        void shouldReturnEmptyPage() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            given(eventRepositoryPort.findAll(pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

            // when
            Page<Event> result = eventService.findAll(pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ======================== findTodayEvents ========================

    @Nested
    @DisplayName("findTodayEvents")
    class FindTodayEvents {

        @Test
        @DisplayName("Deve retornar eventos de hoje")
        void shouldReturnTodayEvents() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Event event = EventFactory.createDefaultEvent();
            Page<Event> page = new PageImpl<>(List.of(event), pageable, 1);

            given(eventRepositoryPort.findTodayEvents(any(), any(), eq(pageable))).willReturn(page);

            // when
            Page<Event> result = eventService.findTodayEvents(pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            then(eventRepositoryPort).should().findTodayEvents(any(), any(), eq(pageable));
        }
    }

    // ======================== findUpcomingEvents ========================

    @Nested
    @DisplayName("findUpcomingEvents")
    class FindUpcomingEvents {

        @Test
        @DisplayName("Deve retornar eventos futuros")
        void shouldReturnUpcomingEvents() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Event event = EventFactory.createDefaultEvent();
            Page<Event> page = new PageImpl<>(List.of(event), pageable, 1);

            given(eventRepositoryPort.findUpcomingEvents(any(), eq(pageable))).willReturn(page);

            // when
            Page<Event> result = eventService.findUpcomingEvents(pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            then(eventRepositoryPort).should().findUpcomingEvents(any(), eq(pageable));
        }
    }

    // ======================== search ========================

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("Deve buscar eventos com todos os filtros")
        void shouldSearchWithAllFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Event event = EventFactory.createDefaultEvent();
            Page<Event> page = new PageImpl<>(List.of(event), pageable, 1);

            String q = "Rock";
            BigDecimal priceMin = BigDecimal.ZERO;
            BigDecimal priceMax = new BigDecimal("100");

            given(eventRepositoryPort.search(eq(q), any(), any(), eq(priceMin), eq(priceMax), eq("Vila Mariana"), eq(pageable)))
                    .willReturn(page);

            // when
            Page<Event> result = eventService.search(q, null, null, priceMin, priceMax, "Vila Mariana", pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            then(eventRepositoryPort).should().search(eq(q), any(), any(), eq(priceMin), eq(priceMax), eq("Vila Mariana"), eq(pageable));
        }

        @Test
        @DisplayName("Deve buscar eventos sem filtros (todos null)")
        void shouldSearchWithoutFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Event> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(eventRepositoryPort.search(null, null, null, null, null, null, pageable))
                    .willReturn(emptyPage);

            // when
            Page<Event> result = eventService.search(null, null, null, null, null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Deve atualizar apenas o nome do evento")
        void shouldUpdateOnlyName() {
            // given
            Event existing = EventFactory.createDefaultEvent();
            EventUpdateDTO dto = EventFactory.createNameOnlyEventUpdateDTO("Festival Atualizado");

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(existing));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.update(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            assertThat(result.getEventName()).isEqualTo("Festival Atualizado");
            assertThat(result.getDescription()).isEqualTo(EventFactory.DEFAULT_DESCRIPTION);
            assertThat(result.getPrice()).isEqualByComparingTo(EventFactory.DEFAULT_PRICE);
        }

        @Test
        @DisplayName("Deve atualizar venue quando venueId é fornecido")
        void shouldUpdateVenueWhenProvided() {
            // given
            Event existing = EventFactory.createDefaultEvent();
            UUID newVenueId = UUID.randomUUID();
            EventUpdateDTO dto = EventFactory.createEventUpdateDTOWithVenue(newVenueId);
            Venue newVenue = Venue.builder().id(newVenueId).name("Novo Local").build();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(existing));
            given(venueService.findById(newVenueId)).willReturn(newVenue);
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.update(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            assertThat(result.getVenue()).isEqualTo(newVenue);
            then(venueService).should().findById(newVenueId);
        }

        @Test
        @DisplayName("Deve atualizar tags quando tagIds é fornecido")
        void shouldUpdateTagsWhenProvided() {
            // given
            Event existing = EventFactory.createDefaultEvent();
            UUID newTagId = UUID.randomUUID();
            EventUpdateDTO dto = EventFactory.createEventUpdateDTOWithTags(Set.of(newTagId));

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(existing));
            willDoNothing().given(tagService).verifyAllTagsExist(Set.of(newTagId));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.update(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            assertThat(result.getTags()).hasSize(1);
            assertThat(result.getTags().iterator().next().getId()).isEqualTo(newTagId);
            then(tagService).should().verifyAllTagsExist(Set.of(newTagId));
        }

        @Test
        @DisplayName("Não deve alterar campos quando DTO está vazio")
        void shouldNotChangeFieldsWhenDtoIsEmpty() {
            // given
            Event existing = EventFactory.createDefaultEvent();
            EventUpdateDTO dto = EventFactory.createEmptyEventUpdateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(existing));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Event result = eventService.update(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            assertThat(result.getEventName()).isEqualTo(EventFactory.DEFAULT_EVENT_NAME);
            assertThat(result.getDescription()).isEqualTo(EventFactory.DEFAULT_DESCRIPTION);
            assertThat(result.getPrice()).isEqualByComparingTo(EventFactory.DEFAULT_PRICE);

            then(venueService).shouldHaveNoInteractions();
            then(tagService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            EventUpdateDTO dto = EventFactory.createNameOnlyEventUpdateDTO("Novo Nome");
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.update(id, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Evento não encontrado");

            then(eventRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar o evento quando ele existe")
        void shouldDeleteEventWhenExists() {
            // given
            Event event = EventFactory.createDefaultEvent();
            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            eventService.delete(EventFactory.DEFAULT_EVENT_ID);

            // then
            then(eventRepositoryPort).should().deleteById(EventFactory.DEFAULT_EVENT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Evento não encontrado");

            then(eventRepositoryPort).should(never()).deleteById(any());
        }
    }

    // ======================== addTagsToEvent ========================

    @Nested
    @DisplayName("addTagsToEvent")
    class AddTagsToEvent {

        @Test
        @DisplayName("Deve adicionar tags ao evento com sucesso")
        void shouldAddTagsSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID newTagId = UUID.randomUUID();
            Set<UUID> tagIds = Set.of(newTagId);
            Set<Tag> newTags = Set.of(EventFactory.createTag(newTagId, "Sertanejo"));

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(tagService.getTagsAndVerify(tagIds)).willReturn(newTags);
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            int originalTagCount = event.getTags().size();

            // when
            eventService.addTagsToEvent(EventFactory.DEFAULT_EVENT_ID, tagIds);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getTags()).hasSize(originalTagCount + 1);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve inicializar set de tags quando é null")
        void shouldInitializeTagsSetWhenNull() {
            // given
            Event event = EventFactory.createEventWithoutTags();
            UUID tagId = UUID.randomUUID();
            Set<UUID> tagIds = Set.of(tagId);
            Set<Tag> tags = Set.of(EventFactory.createTag(tagId, "MPB"));

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(tagService.getTagsAndVerify(tagIds)).willReturn(tags);
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            eventService.addTagsToEvent(EventFactory.DEFAULT_EVENT_ID, tagIds);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getTags()).hasSize(1);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.addTagsToEvent(id, Set.of(UUID.randomUUID())))
                    .isInstanceOf(ResourceNotFoundException.class);

            then(eventRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Deve propagar exceção quando tags não existem")
        void shouldPropagateExceptionWhenTagsNotFound() {
            // given
            Event event = EventFactory.createDefaultEvent();
            Set<UUID> tagIds = Set.of(UUID.randomUUID());

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(tagService.getTagsAndVerify(tagIds))
                    .willThrow(new ResourceNotFoundException("One or more tags not found"));

            // when / then
            assertThatThrownBy(() -> eventService.addTagsToEvent(EventFactory.DEFAULT_EVENT_ID, tagIds))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("One or more tags not found");

            then(eventRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== removeTagFromEvent ========================

    @Nested
    @DisplayName("removeTagFromEvent")
    class RemoveTagFromEvent {

        @Test
        @DisplayName("Deve remover tag do evento quando ela está associada")
        void shouldRemoveTagSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID tagId = event.getTags().iterator().next().getId();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            int originalSize = event.getTags().size();

            // when
            eventService.removeTagFromEvent(EventFactory.DEFAULT_EVENT_ID, tagId);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getTags()).hasSize(originalSize - 1);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Não deve salvar quando a tag não estava associada")
        void shouldNotSaveWhenTagNotAssociated() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID unknownTagId = UUID.randomUUID();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            eventService.removeTagFromEvent(EventFactory.DEFAULT_EVENT_ID, unknownTagId);

            // then
            then(eventRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Não deve salvar quando tags é null")
        void shouldNotSaveWhenTagsIsNull() {
            // given
            Event event = EventFactory.createEventWithoutTags();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            eventService.removeTagFromEvent(EventFactory.DEFAULT_EVENT_ID, UUID.randomUUID());

            // then
            then(eventRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== addPictureToEvent ========================

    @Nested
    @DisplayName("addPictureToEvent")
    class AddPictureToEvent {

        @Test
        @DisplayName("Deve adicionar picture ao evento com sucesso")
        void shouldAddPictureSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();
            String newPicUrl = "http://localhost:8080/uploads/events/new_pic.jpg";

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            int originalSize = event.getPictures().size();

            // when
            eventService.addPictureToEvent(EventFactory.DEFAULT_EVENT_ID, newPicUrl);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getPictures()).hasSize(originalSize + 1);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve inicializar set de pictures quando é null")
        void shouldInitializePicturesSetWhenNull() {
            // given
            Event event = EventFactory.createEventWithoutPictures();
            String picUrl = "http://localhost:8080/uploads/events/first_pic.jpg";

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            eventService.addPictureToEvent(EventFactory.DEFAULT_EVENT_ID, picUrl);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getPictures()).hasSize(1);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.addPictureToEvent(id, "url"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ======================== removePictureFromEvent ========================

    @Nested
    @DisplayName("removePictureFromEvent")
    class RemovePictureFromEvent {

        @Test
        @DisplayName("Deve remover picture quando ela pertence ao evento")
        void shouldRemovePictureSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            eventService.removePictureFromEvent(EventFactory.DEFAULT_EVENT_ID, EventFactory.DEFAULT_PICTURE_ID);

            // then
            then(eventRepositoryPort).should().deletePictureById(EventFactory.DEFAULT_PICTURE_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando picture não pertence ao evento")
        void shouldThrowExceptionWhenPictureNotFound() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID unknownPicId = UUID.randomUUID();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.removePictureFromEvent(EventFactory.DEFAULT_EVENT_ID, unknownPicId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Picture not found for this event");

            then(eventRepositoryPort).should(never()).deletePictureById(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando pictures é null")
        void shouldThrowExceptionWhenPicturesIsNull() {
            // given
            Event event = EventFactory.createEventWithoutPictures();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.removePictureFromEvent(EventFactory.DEFAULT_EVENT_ID, UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Picture not found for this event");
        }
    }

    // ======================== addScheduleToEvent ========================

    @Nested
    @DisplayName("addScheduleToEvent")
    class AddScheduleToEvent {

        @Test
        @DisplayName("Deve adicionar schedule ao evento com sucesso")
        void shouldAddScheduleSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();
            EventScheduleCreateDTO dto = EventFactory.createDefaultScheduleCreateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            int originalSize = event.getSchedules().size();

            // when
            eventService.addScheduleToEvent(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();
            assertThat(captured.getSchedules()).hasSize(originalSize + 1);
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve inicializar set de schedules quando é null")
        void shouldInitializeSchedulesSetWhenNull() {
            // given
            Event event = EventFactory.createEventWithoutSchedules();
            EventScheduleCreateDTO dto = EventFactory.createDefaultScheduleCreateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            eventService.addScheduleToEvent(EventFactory.DEFAULT_EVENT_ID, dto);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            assertThat(eventCaptor.getValue().getSchedules()).hasSize(1);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowExceptionWhenEventNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(eventRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> eventService.addScheduleToEvent(id, EventFactory.createDefaultScheduleCreateDTO()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ======================== updateSchedule ========================

    @Nested
    @DisplayName("updateSchedule")
    class UpdateSchedule {

        @Test
        @DisplayName("Deve atualizar todos os campos do schedule")
        void shouldUpdateScheduleSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();
            EventScheduleUpdateDTO dto = EventFactory.createFullScheduleUpdateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            eventService.updateSchedule(EventFactory.DEFAULT_EVENT_ID, EventFactory.DEFAULT_SCHEDULE_ID, dto);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            Event captured = eventCaptor.getValue();

            EventSchedule updatedSchedule = captured.getSchedules().stream()
                    .filter(s -> s.getId().equals(EventFactory.DEFAULT_SCHEDULE_ID))
                    .findFirst().orElseThrow();

            assertThat(updatedSchedule.getTitle()).isEqualTo("Show Principal — Banda X");
            assertThat(updatedSchedule.getDescription()).isEqualTo("Show principal da noite com a Banda X.");
            assertThat(captured.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve atualizar apenas o título do schedule")
        void shouldUpdateOnlyTitle() {
            // given
            Event event = EventFactory.createDefaultEvent();
            EventScheduleUpdateDTO dto = EventFactory.createTitleOnlyScheduleUpdateDTO("Novo Título");

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));
            given(eventRepositoryPort.save(any(Event.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            eventService.updateSchedule(EventFactory.DEFAULT_EVENT_ID, EventFactory.DEFAULT_SCHEDULE_ID, dto);

            // then
            then(eventRepositoryPort).should().save(eventCaptor.capture());
            EventSchedule schedule = eventCaptor.getValue().getSchedules().stream()
                    .filter(s -> s.getId().equals(EventFactory.DEFAULT_SCHEDULE_ID))
                    .findFirst().orElseThrow();

            assertThat(schedule.getTitle()).isEqualTo("Novo Título");
            assertThat(schedule.getDescription()).isEqualTo(EventFactory.DEFAULT_SCHEDULE_DESCRIPTION);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando schedule não pertence ao evento")
        void shouldThrowExceptionWhenScheduleNotFound() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID unknownScheduleId = UUID.randomUUID();
            EventScheduleUpdateDTO dto = EventFactory.createFullScheduleUpdateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.updateSchedule(
                    EventFactory.DEFAULT_EVENT_ID, unknownScheduleId, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Programação não encontrada");

            then(eventRepositoryPort).should(never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando schedules é null")
        void shouldThrowExceptionWhenSchedulesIsNull() {
            // given
            Event event = EventFactory.createEventWithoutSchedules();
            EventScheduleUpdateDTO dto = EventFactory.createFullScheduleUpdateDTO();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.updateSchedule(
                    EventFactory.DEFAULT_EVENT_ID, UUID.randomUUID(), dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Programação não encontrada");
        }
    }

    // ======================== removeScheduleFromEvent ========================

    @Nested
    @DisplayName("removeScheduleFromEvent")
    class RemoveScheduleFromEvent {

        @Test
        @DisplayName("Deve remover schedule quando ele pertence ao evento")
        void shouldRemoveScheduleSuccessfully() {
            // given
            Event event = EventFactory.createDefaultEvent();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when
            eventService.removeScheduleFromEvent(EventFactory.DEFAULT_EVENT_ID, EventFactory.DEFAULT_SCHEDULE_ID);

            // then
            then(eventRepositoryPort).should().deleteScheduleById(EventFactory.DEFAULT_SCHEDULE_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando schedule não pertence ao evento")
        void shouldThrowExceptionWhenScheduleNotFound() {
            // given
            Event event = EventFactory.createDefaultEvent();
            UUID unknownId = UUID.randomUUID();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.removeScheduleFromEvent(EventFactory.DEFAULT_EVENT_ID, unknownId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Programação não encontrada");

            then(eventRepositoryPort).should(never()).deleteScheduleById(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando schedules é null")
        void shouldThrowExceptionWhenSchedulesIsNull() {
            // given
            Event event = EventFactory.createEventWithoutSchedules();

            given(eventRepositoryPort.findById(EventFactory.DEFAULT_EVENT_ID)).willReturn(Optional.of(event));

            // when / then
            assertThatThrownBy(() -> eventService.removeScheduleFromEvent(
                    EventFactory.DEFAULT_EVENT_ID, UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Programação não encontrada");
        }
    }
}
