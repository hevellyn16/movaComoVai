package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.infrastructure.persistence.entity.TagEntity;
import org.springframework.stereotype.Component;

@Component
public class TagConverter {

    public static Tag entityToDomain(TagEntity tagEntity) {
        if (tagEntity == null) return null;

        return Tag.builder()
                .id(tagEntity.getId())
                .tagName(tagEntity.getTagName())
                .build();
    }

    public static TagEntity domainToEntity(Tag tag) {
        if (tag == null) return null;

        return TagEntity.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .build();
    }

    public static TagResponseDTO domainToResponse(Tag tag) {
        if (tag == null) return null;

        return TagResponseDTO.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .build();
    }

    public static Tag createDTOToDomain(TagCreateDTO dto) {
        if (dto == null) return null;

        return Tag.builder()
                .tagName(dto.tagName().trim())
                .build();
    }

    public static Tag updateTagFromDTO(Tag tag, TagUpdateDTO dto) {
        if (tag == null || dto == null) return null;

        tag.setTagName(dto.tagName().trim());
        return tag;
    }

}
