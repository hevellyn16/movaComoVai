package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.port.TagRepositoryPort;
import com.eng.software.mova.infrastructure.persistence.entity.TagEntity;
import com.eng.software.mova.shared.utils.TagConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TagRepository implements TagRepositoryPort {
    private final TagJpaRepository tagJpaRepository;

    @Override
    public Optional<Tag> findById(UUID id) {
        return tagJpaRepository.findById(id)
                .map(TagConverter::entityToDomain);
    }

    @Override
    public Optional<Tag> findByTagName(String tagName) {
        return tagJpaRepository.findByTagNameIgnoreCase(tagName)
                .map(TagConverter::entityToDomain);
    }

    @Override
    public List<Tag> findAll() {
        return tagJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "tagName"))
                .stream()
                .map(TagConverter::entityToDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return tagJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByTagName(String tagName) {
        return tagJpaRepository.existsByTagNameIgnoreCase(tagName);
    }

    @Override
    public Tag save(Tag tag) {
        TagEntity savedTag = tagJpaRepository.save(TagConverter.domainToEntity(tag));
        return TagConverter.entityToDomain(savedTag);
    }

    @Override
    public Tag update(Tag tag) {
        TagEntity savedTag = tagJpaRepository.save(TagConverter.domainToEntity(tag));
        return TagConverter.entityToDomain(savedTag);
    }

    @Override
    public void deleteById(UUID id) {
        tagJpaRepository.deleteById(id);
    }

    @Override
    public long countByIdIn(Set<UUID> ids) {
        return tagJpaRepository.countByIdIn(ids);
    }
}
