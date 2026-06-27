package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.picture.EventPictureResponseDTO;
import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.EventPicture;
import com.eng.software.mova.domain.port.EventPictureRepositoryPort;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.EventPictureConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPictureService {
    private final EventPictureRepositoryPort eventPictureRepositoryPort;
    private final EventRepositoryPort eventRepository;
    private final FileStoragePort fileStoragePort;

    public EventPictureResponseDTO findById(UUID id) {
        EventPicture eventPicture = eventPictureRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event picture not found with id: " + id));

        return EventPictureConverter.domainToResponse(eventPicture);
    }

    public Page<EventPictureResponseDTO> findByEventId(UUID eventId, Pageable pageable) {
        Page<EventPicture> eventPictures = eventPictureRepositoryPort.findByEventId(eventId, pageable);

        return eventPictures.map(EventPictureConverter::domainToResponse);
    }



    @Transactional
    public void deleteById(UUID id) {
        if (eventPictureRepositoryPort.findById(id).isEmpty())
            throw new ResourceNotFoundException("Event picture not found with id: " + id);

        eventPictureRepositoryPort.deleteById(id);
    }

    @Transactional
    public EventPictureResponseDTO savePicture(UUID eventId, MultipartFile file) {
        String imageUrl = fileStoragePort.uploadFile(file);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        EventPicture picture = EventPicture.builder()
                .eventId(event.getId())
                .pictureUrl(imageUrl)
                .eventId(event.getId())
                .build();

        return EventPictureConverter.domainToResponse(eventPictureRepositoryPort.save(picture));
    }
}
