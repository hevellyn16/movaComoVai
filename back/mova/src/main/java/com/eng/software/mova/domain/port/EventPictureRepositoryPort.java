package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.EventPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EventPictureRepositoryPort {
    Optional<EventPicture> findById(UUID id);
    Page<EventPicture> findByEventId(UUID eventId, Pageable pageable);
    EventPicture save(EventPicture eventPicture);
    void deleteById(UUID id);
}
