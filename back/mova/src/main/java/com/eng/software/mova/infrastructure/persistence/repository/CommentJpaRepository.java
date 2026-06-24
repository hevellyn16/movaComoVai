package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, UUID> {
     @EntityGraph(attributePaths = {"user"})
     Page<CommentEntity> findByEventId(UUID eventId, Pageable pageable);

     @Override
     @EntityGraph(attributePaths = {"user"})
     Optional<CommentEntity> findById(UUID id);
}
