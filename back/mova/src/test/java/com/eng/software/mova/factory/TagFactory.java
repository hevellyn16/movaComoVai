package com.eng.software.mova.factory;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.domain.model.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Factory para criação de objetos de teste relacionados a Tag.
 * Centraliza a construção de entidades de domínio e DTOs,
 * garantindo dados consistentes e reutilizáveis em toda a suíte de testes.
 */
public final class TagFactory {

    public static final UUID DEFAULT_ID = UUID.fromString("e5f6a7b8-c9d0-1234-efab-345678901234");
    public static final UUID SECOND_ID = UUID.fromString("f6a7b8c9-d0e1-2345-fabc-456789012345");
    public static final String DEFAULT_TAG_NAME = "Música Ao Vivo";
    public static final String SECOND_TAG_NAME = "Rock";

    private TagFactory() {
        // Utility class — impede instanciação
    }

    // ======================== DOMAIN MODEL ========================

    /**
     * Cria uma Tag de domínio padrão.
     */
    public static Tag createDefaultTag() {
        return Tag.builder()
                .id(DEFAULT_ID)
                .tagName(DEFAULT_TAG_NAME)
                .build();
    }

    /**
     * Cria uma Tag com id e nome customizados.
     */
    public static Tag createTag(UUID id, String name) {
        return Tag.builder()
                .id(id)
                .tagName(name)
                .build();
    }

    /**
     * Cria uma segunda Tag distinta para cenários com múltiplas tags.
     */
    public static Tag createSecondTag() {
        return Tag.builder()
                .id(SECOND_ID)
                .tagName(SECOND_TAG_NAME)
                .build();
    }

    /**
     * Cria um Set com a tag padrão e a segunda tag.
     */
    public static Set<Tag> createDefaultTagSet() {
        return new HashSet<>(Set.of(createDefaultTag(), createSecondTag()));
    }

    // ======================== CREATE DTO ========================

    /**
     * Cria um TagCreateDTO padrão válido.
     */
    public static TagCreateDTO createDefaultTagCreateDTO() {
        return new TagCreateDTO(DEFAULT_TAG_NAME);
    }

    /**
     * Cria um TagCreateDTO com nome customizado.
     */
    public static TagCreateDTO createTagCreateDTO(String tagName) {
        return new TagCreateDTO(tagName);
    }

    /**
     * Cria um TagCreateDTO com espaços ao redor (para testar trim).
     */
    public static TagCreateDTO createTagCreateDTOWithSpaces() {
        return new TagCreateDTO("  " + DEFAULT_TAG_NAME + "  ");
    }

    // ======================== UPDATE DTO ========================

    /**
     * Cria um TagUpdateDTO padrão.
     */
    public static TagUpdateDTO createDefaultTagUpdateDTO() {
        return new TagUpdateDTO("Rock Alternativo");
    }

    /**
     * Cria um TagUpdateDTO com nome customizado.
     */
    public static TagUpdateDTO createTagUpdateDTO(String tagName) {
        return new TagUpdateDTO(tagName);
    }

    /**
     * Cria um TagUpdateDTO com espaços ao redor (para testar trim).
     */
    public static TagUpdateDTO createTagUpdateDTOWithSpaces(String tagName) {
        return new TagUpdateDTO("  " + tagName + "  ");
    }

    // ======================== RESPONSE DTO ========================

    /**
     * Cria um TagResponseDTO correspondente à Tag padrão.
     */
    public static TagResponseDTO createDefaultTagResponseDTO() {
        return TagResponseDTO.builder()
                .id(DEFAULT_ID)
                .tagName(DEFAULT_TAG_NAME)
                .build();
    }
}
