package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventPictureRepositoryTest {

    @Mock
    private EventPictureJpaRepository eventPictureRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private EventPictureRepository eventPictureRepositoryAdapter;

    @Test
    public void shouldReturnMappedEventPicture_whenFindByIdExists() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPictureEntity entity = EventPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .event(EventEntity.builder().id(eventId).build())
                .build();

        when(eventPictureRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<EventPicture> result = eventPictureRepositoryAdapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getEventId()).isEqualTo(eventId);
        assertThat(result.get().getPictureUrl()).isEqualTo(url);
        verify(eventPictureRepository, times(1)).findById(id);
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindByIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(eventPictureRepository.findById(id)).thenReturn(Optional.empty());

        Optional<EventPicture> result = eventPictureRepositoryAdapter.findById(id);

        assertThat(result).isEmpty();
        verify(eventPictureRepository, times(1)).findById(id);
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }

    @Test
    public void shouldReturnMappedPage_whenFindByEventId() {
        UUID eventId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPictureEntity entity = EventPictureEntity.builder()
                .id(pictureId)
                .pictureUrl(url)
                .event(EventEntity.builder().id(eventId).build())
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<EventPictureEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(eventPictureRepository.findByEventId(eventId, pageable)).thenReturn(entityPage);

        Page<EventPicture> result = eventPictureRepositoryAdapter.findByEventId(eventId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(pictureId);
        assertThat(result.getContent().getFirst().getEventId()).isEqualTo(eventId);
        verify(eventPictureRepository, times(1)).findByEventId(eventId, pageable);
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }

    @Test
    public void shouldSaveAndReturnMappedEventPicture_whenSaveIsCalled() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPicture domain = EventPicture.builder()
                .id(id)
                .eventId(eventId)
                .pictureUrl(url)
                .build();

        EventPictureEntity savedEntity = EventPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .event(EventEntity.builder().id(eventId).build())
                .build();

        when(eventPictureRepository.save(any(EventPictureEntity.class))).thenReturn(savedEntity);

        EventPicture result = eventPictureRepositoryAdapter.save(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getPictureUrl()).isEqualTo(url);
        verify(eventPictureRepository, times(1)).save(any(EventPictureEntity.class));
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }

    @Test
    public void shouldDeleteEventPictureAndFile_whenDeleteByIdIsCalled() {
        UUID id = UUID.randomUUID();
        String url = "https://storage.example.com/event-picture.jpg";

        EventPictureEntity entity = EventPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .event(EventEntity.builder().id(UUID.randomUUID()).build())
                .build();

        when(eventPictureRepository.findById(id)).thenReturn(Optional.of(entity));

        eventPictureRepositoryAdapter.deleteById(id);

        verify(eventPictureRepository, times(1)).findById(id);
        verify(eventPictureRepository, times(1)).delete(entity);
        verify(fileStoragePort, times(1)).deleteFile(url);
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }

    @Test
    public void shouldThrowException_whenDeleteByIdEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(eventPictureRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventPictureRepositoryAdapter.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Event picture not found with id: " + id);

        verify(eventPictureRepository, times(1)).findById(id);
        verify(eventPictureRepository, never()).delete(any());
        verify(fileStoragePort, never()).deleteFile(any());
        verifyNoMoreInteractions(eventPictureRepository, fileStoragePort);
    }
}
