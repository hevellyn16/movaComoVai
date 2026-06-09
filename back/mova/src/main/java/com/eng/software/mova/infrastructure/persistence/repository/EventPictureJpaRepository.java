package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.EventPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface EventPictureJpaRepository extends JpaRepository<EventPictureEntity, UUID> {
    Set<EventPictureEntity> findByEventId(UUID eventId);
}
