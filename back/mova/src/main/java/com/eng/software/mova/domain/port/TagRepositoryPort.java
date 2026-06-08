package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagRepositoryPort {
    Optional<Tag> findById(UUID id);
    Optional<Tag> findByTagName(String tagName);
    List<Tag> findAll();
    boolean existsById(UUID id);
    boolean existsByTagName(String tagName);
    Tag save(Tag tag);
    Tag update(Tag tag);
    void deleteById(UUID id);
    long countByIdIn(Set<UUID> ids);
}
