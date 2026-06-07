package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagJpaRepository extends JpaRepository<TagEntity, UUID> {
    Optional<TagEntity> findByTagNameIgnoreCase(String tagName);
    boolean existsByTagNameIgnoreCase(String tagName);
}
