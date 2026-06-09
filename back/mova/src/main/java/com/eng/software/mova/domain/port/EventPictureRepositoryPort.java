package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.EventPicture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EventPictureRepositoryPort {
    Optional<EventPicture> findById(UUID id);
    Set<EventPicture> findByEventId(UUID eventId);
    EventPicture save(EventPicture eventPicture);
    void deleteById(UUID id);
}
