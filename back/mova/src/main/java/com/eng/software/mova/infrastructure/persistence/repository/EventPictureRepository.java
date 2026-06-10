package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.port.EventPictureRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import com.eng.software.mova.shared.utils.EventPictureConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventPictureRepository implements EventPictureRepositoryPort {
    private final EventPictureJpaRepository eventPictureRepository;
    private final FileStoragePort fileStoragePort;

    @Override
    public Optional<EventPicture> findById(UUID id) {
        Optional<EventPictureEntity> eventPictureEntity = eventPictureRepository.findById(id);
        return eventPictureEntity.map(EventPictureConverter::entityToDomain);
    }

    @Override
    public Set<EventPicture> findByEventId(UUID eventId) {
        return eventPictureRepository.findByEventId(eventId).stream()
                .map(EventPictureConverter::entityToDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public EventPicture save(EventPicture eventPicture) {
        EventPictureEntity eventPictureEntity = EventPictureConverter.domainToEntity(eventPicture);

        return EventPictureConverter.entityToDomain(eventPictureRepository.save(eventPictureEntity));
    }

    @Override
    public void deleteById(UUID id) {
        EventPictureEntity pictureEntity = eventPictureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event picture not found with id: " + id));

        eventPictureRepository.delete(pictureEntity);

        fileStoragePort.deleteFile(pictureEntity.getPictureUrl());
    }
}
