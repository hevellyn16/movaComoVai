package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.port.EventPictureRepositoryPort;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPictureServiceTest {

    @Mock
    private EventPictureRepositoryPort eventPictureRepositoryPort;

    @Mock
    private EventRepositoryPort eventRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private EventPictureService eventPictureService;

    // ─── findById ───────────────────────────────────────────────────────────

    @Test
    public void shouldReturnEventPictureResponseDTO_whenFindByIdExists() {
        UUID eventId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPicture picture = EventPicture.builder()
                .id(pictureId)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        when(eventPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        EventPictureResponseDTO response = eventPictureService.findById(pictureId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(pictureId);
        assertThat(response.eventId()).isEqualTo(eventId);
        assertThat(response.pictureUrl()).isEqualTo(url);
        verify(eventPictureRepositoryPort, times(1)).findById(pictureId);
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenFindByIdDoesNotExist() {
        UUID pictureId = UUID.randomUUID();
        when(eventPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventPictureService.findById(pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event picture not found with id: " + pictureId);

        verify(eventPictureRepositoryPort, times(1)).findById(pictureId);
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    // ─── findByEventId ──────────────────────────────────────────────────────

    @Test
    public void shouldReturnPageOfResponseDTO_whenFindByEventId() {
        UUID eventId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";
        PageRequest pageable = PageRequest.of(0, 10);

        EventPicture picture = EventPicture.builder()
                .id(pictureId)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        Page<EventPicture> page = new PageImpl<>(List.of(picture), pageable, 1);
        when(eventPictureRepositoryPort.findByEventId(eventId, pageable)).thenReturn(page);

        Page<EventPictureResponseDTO> result = eventPictureService.findByEventId(eventId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(pictureId);
        assertThat(result.getContent().getFirst().eventId()).isEqualTo(eventId);
        verify(eventPictureRepositoryPort, times(1)).findByEventId(eventId, pageable);
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    // ─── deleteById ─────────────────────────────────────────────────────────

    @Test
    public void shouldDelete_whenDeleteByIdIsCalledWithExistingPicture() {
        UUID pictureId = UUID.randomUUID();

        EventPicture picture = EventPicture.builder()
                .id(pictureId)
                .eventId(UUID.randomUUID())
                .pictureUrl("url")
                .build();

        when(eventPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        eventPictureService.deleteById(pictureId);

        verify(eventPictureRepositoryPort, times(1)).findById(pictureId);
        verify(eventPictureRepositoryPort, times(1)).deleteById(pictureId);
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenDeleteByIdDoesNotExist() {
        UUID pictureId = UUID.randomUUID();
        when(eventPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventPictureService.deleteById(pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event picture not found with id: " + pictureId);

        verify(eventPictureRepositoryPort, times(1)).findById(pictureId);
        verify(eventPictureRepositoryPort, never()).deleteById(any());
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    // ─── savePicture ────────────────────────────────────────────────────────

    @Test
    public void shouldSaveAndReturnResponseDTO_whenSavePictureIsCalledWithValidInput() {
        UUID eventId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "event-picture.jpg", "image/jpeg", "fake-content".getBytes());

        Event event = Event.builder()
                .id(eventId)
                .eventName("Evento Teste")
                .build();

        EventPicture savedPicture = EventPicture.builder()
                .id(pictureId)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        when(fileStoragePort.uploadFile(file)).thenReturn(url);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventPictureRepositoryPort.save(any(EventPicture.class))).thenReturn(savedPicture);

        EventPictureResponseDTO response = eventPictureService.savePicture(eventId, file);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(pictureId);
        assertThat(response.eventId()).isEqualTo(eventId);
        assertThat(response.pictureUrl()).isEqualTo(url);
        verify(fileStoragePort, times(1)).uploadFile(file);
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventPictureRepositoryPort, times(1)).save(any(EventPicture.class));
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenSavePictureEventNotFound() {
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "event-picture.jpg", "image/jpeg", "fake-content".getBytes());

        when(fileStoragePort.uploadFile(file)).thenReturn(url);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventPictureService.savePicture(eventId, file))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with id: " + eventId);

        verify(fileStoragePort, times(1)).uploadFile(file);
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventPictureRepositoryPort, never()).save(any());
        verifyNoMoreInteractions(eventPictureRepositoryPort, eventRepository, fileStoragePort);
    }
}
