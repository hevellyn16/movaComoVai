package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.port.TagRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.TagConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {
    private final TagRepositoryPort tagRepositoryPort;

    @Transactional(readOnly = true)
    public List<TagResponseDTO> findAll() {
        return tagRepositoryPort.findAll()
                .stream()
                .map(TagConverter::domainToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponseDTO findById(UUID id) {
        Tag tag = tagRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        return TagConverter.domainToResponse(tag);
    }

    public TagResponseDTO create(TagCreateDTO dto) {
        String normalizedTagName = dto.tagName().trim();

        if (tagRepositoryPort.existsByTagName(normalizedTagName)) {
            throw new ResourceAlreadyExistsException("Tag already exists with name: " + normalizedTagName);
        }

        Tag tag = TagConverter.createDTOToDomain(dto);
        return TagConverter.domainToResponse(tagRepositoryPort.save(tag));
    }

    public TagResponseDTO update(UUID id, TagUpdateDTO dto) {
        Tag existingTag = tagRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        String normalizedTagName = dto.tagName().trim();

        boolean tagNameChanged = !existingTag.getTagName().equalsIgnoreCase(normalizedTagName);
        if (tagNameChanged && tagRepositoryPort.existsByTagName(normalizedTagName)) {
            throw new ResourceAlreadyExistsException("Tag already exists with name: " + normalizedTagName);
        }

        Tag updatedTag = TagConverter.updateTagFromDTO(existingTag, dto);
        return TagConverter.domainToResponse(tagRepositoryPort.update(updatedTag));
    }

    public void delete(UUID id) {
        if (!tagRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found with id: " + id);
        }

        tagRepositoryPort.deleteById(id);
    }

    public Set<Tag> getTagsAndVerify(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Tag> tags = tagRepositoryPort.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found with ids: " + tagIds);
        }

        return tags;
    }

    public void verifyAllTagsExist(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        long existingTagsCount = tagRepositoryPort.countByIdIn(tagIds);

        if (existingTagsCount != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found with ids: " + tagIds);
        }
    }
}
