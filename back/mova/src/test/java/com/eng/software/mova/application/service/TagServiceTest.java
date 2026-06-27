package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.tag.TagCreateDTO;
import com.eng.software.mova.application.dto.tag.TagResponseDTO;
import com.eng.software.mova.application.dto.tag.TagUpdateDTO;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.port.TagRepositoryPort;
import com.eng.software.mova.factory.TagFactory;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários do {@link TagService}.
 * <p>
 * Utiliza Mockito com BDD style (given/when/then) e AssertJ para asserções fluidas.
 * Cada método público do service possui um {@link Nested} group próprio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TagService — Testes Unitários")
class TagServiceTest {

    @Mock
    private TagRepositoryPort tagRepositoryPort;

    @InjectMocks
    private TagService tagService;

    @Captor
    private ArgumentCaptor<Tag> tagCaptor;

    // ======================== findAll ========================

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Deve retornar lista de tags")
        void shouldReturnListOfTags() {
            // given
            Tag tag1 = TagFactory.createDefaultTag();
            Tag tag2 = TagFactory.createSecondTag();

            given(tagRepositoryPort.findAll()).willReturn(List.of(tag1, tag2));

            // when
            List<TagResponseDTO> result = tagService.findAll();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).tagName()).isEqualTo(TagFactory.DEFAULT_TAG_NAME);
            assertThat(result.get(1).tagName()).isEqualTo(TagFactory.SECOND_TAG_NAME);

            then(tagRepositoryPort).should().findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há tags")
        void shouldReturnEmptyListWhenNoTags() {
            // given
            given(tagRepositoryPort.findAll()).willReturn(List.of());

            // when
            List<TagResponseDTO> result = tagService.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }

    // ======================== findById ========================

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve retornar TagResponseDTO quando a tag existe")
        void shouldReturnTagWhenExists() {
            // given
            Tag tag = TagFactory.createDefaultTag();
            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(tag));

            // when
            TagResponseDTO result = tagService.findById(TagFactory.DEFAULT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(TagFactory.DEFAULT_ID);
            assertThat(result.tagName()).isEqualTo(TagFactory.DEFAULT_TAG_NAME);

            then(tagRepositoryPort).should().findById(TagFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a tag não existe")
        void shouldThrowExceptionWhenTagNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(tagRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> tagService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Tag not found with id: " + id);
        }
    }

    // ======================== create ========================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar tag com sucesso quando o nome é único")
        void shouldCreateTagSuccessfully() {
            // given
            TagCreateDTO dto = TagFactory.createDefaultTagCreateDTO();

            given(tagRepositoryPort.existsByTagName(dto.tagName().trim())).willReturn(false);
            given(tagRepositoryPort.save(any(Tag.class))).willAnswer(inv -> {
                Tag saved = inv.getArgument(0);
                saved.setId(TagFactory.DEFAULT_ID);
                return saved;
            });

            // when
            TagResponseDTO result = tagService.create(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tagName()).isEqualTo(TagFactory.DEFAULT_TAG_NAME);

            then(tagRepositoryPort).should().existsByTagName(dto.tagName().trim());
            then(tagRepositoryPort).should().save(any(Tag.class));
        }

        @Test
        @DisplayName("Deve fazer trim do nome da tag antes de verificar unicidade")
        void shouldTrimTagNameBeforeChecking() {
            // given
            TagCreateDTO dto = TagFactory.createTagCreateDTOWithSpaces();

            given(tagRepositoryPort.existsByTagName(TagFactory.DEFAULT_TAG_NAME)).willReturn(false);
            given(tagRepositoryPort.save(any(Tag.class))).willAnswer(inv -> {
                Tag saved = inv.getArgument(0);
                saved.setId(TagFactory.DEFAULT_ID);
                return saved;
            });

            // when
            tagService.create(dto);

            // then
            then(tagRepositoryPort).should().existsByTagName(TagFactory.DEFAULT_TAG_NAME);
            then(tagRepositoryPort).should().save(tagCaptor.capture());
            assertThat(tagCaptor.getValue().getTagName()).isEqualTo(TagFactory.DEFAULT_TAG_NAME);
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando o nome já existe")
        void shouldThrowExceptionWhenNameAlreadyExists() {
            // given
            TagCreateDTO dto = TagFactory.createDefaultTagCreateDTO();
            given(tagRepositoryPort.existsByTagName(dto.tagName().trim())).willReturn(true);

            // when / then
            assertThatThrownBy(() -> tagService.create(dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Tag already exists with name: " + dto.tagName().trim());

            then(tagRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Deve atualizar o nome da tag com sucesso")
        void shouldUpdateTagSuccessfully() {
            // given
            Tag existing = TagFactory.createDefaultTag();
            TagUpdateDTO dto = TagFactory.createDefaultTagUpdateDTO();

            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(tagRepositoryPort.existsByTagName("Rock Alternativo")).willReturn(false);
            given(tagRepositoryPort.update(any(Tag.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            TagResponseDTO result = tagService.update(TagFactory.DEFAULT_ID, dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tagName()).isEqualTo("Rock Alternativo");

            then(tagRepositoryPort).should().update(any(Tag.class));
        }

        @Test
        @DisplayName("Deve fazer trim do nome ao atualizar")
        void shouldTrimTagNameOnUpdate() {
            // given
            Tag existing = TagFactory.createDefaultTag();
            TagUpdateDTO dto = TagFactory.createTagUpdateDTOWithSpaces("Sertanejo");

            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(tagRepositoryPort.existsByTagName("Sertanejo")).willReturn(false);
            given(tagRepositoryPort.update(any(Tag.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            TagResponseDTO result = tagService.update(TagFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.tagName()).isEqualTo("Sertanejo");
            then(tagRepositoryPort).should().existsByTagName("Sertanejo");
        }

        @Test
        @DisplayName("Deve permitir update quando o nome não mudou (case insensitive)")
        void shouldAllowUpdateWhenNameUnchanged() {
            // given
            Tag existing = TagFactory.createDefaultTag();
            TagUpdateDTO dto = TagFactory.createTagUpdateDTO(TagFactory.DEFAULT_TAG_NAME);

            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            // tagNameChanged = false, portanto existsByTagName NÃO é chamado
            given(tagRepositoryPort.update(any(Tag.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            TagResponseDTO result = tagService.update(TagFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.tagName()).isEqualTo(TagFactory.DEFAULT_TAG_NAME);
            then(tagRepositoryPort).should(never()).existsByTagName(anyString());
            then(tagRepositoryPort).should().update(any(Tag.class));
        }

        @Test
        @DisplayName("Deve permitir update quando o nome muda apenas em case")
        void shouldAllowUpdateWhenOnlyCaseChanges() {
            // given
            Tag existing = TagFactory.createDefaultTag(); // "Música Ao Vivo"
            TagUpdateDTO dto = TagFactory.createTagUpdateDTO("música ao vivo");

            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            // equalsIgnoreCase → false (nomes diferem em case), mas tagNameChanged
            // verifica com equalsIgnoreCase, então tagNameChanged = false
            given(tagRepositoryPort.update(any(Tag.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            TagResponseDTO result = tagService.update(TagFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.tagName()).isEqualTo("música ao vivo");
            then(tagRepositoryPort).should(never()).existsByTagName(anyString());
        }

        @Test
        @DisplayName("Deve lançar ResourceAlreadyExistsException quando novo nome já existe")
        void shouldThrowExceptionWhenNewNameAlreadyExists() {
            // given
            Tag existing = TagFactory.createDefaultTag();
            TagUpdateDTO dto = TagFactory.createDefaultTagUpdateDTO(); // "Rock Alternativo"

            given(tagRepositoryPort.findById(TagFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(tagRepositoryPort.existsByTagName("Rock Alternativo")).willReturn(true);

            // when / then
            assertThatThrownBy(() -> tagService.update(TagFactory.DEFAULT_ID, dto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Tag already exists with name: Rock Alternativo");

            then(tagRepositoryPort).should(never()).update(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a tag não existe")
        void shouldThrowExceptionWhenTagNotFound() {
            // given
            UUID id = UUID.randomUUID();
            TagUpdateDTO dto = TagFactory.createDefaultTagUpdateDTO();

            given(tagRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> tagService.update(id, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Tag not found with id: " + id);

            then(tagRepositoryPort).should(never()).update(any());
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar a tag quando ela existe")
        void shouldDeleteTagWhenExists() {
            // given
            given(tagRepositoryPort.existsById(TagFactory.DEFAULT_ID)).willReturn(true);

            // when
            tagService.delete(TagFactory.DEFAULT_ID);

            // then
            then(tagRepositoryPort).should().deleteById(TagFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a tag não existe")
        void shouldThrowExceptionWhenTagNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(tagRepositoryPort.existsById(id)).willReturn(false);

            // when / then
            assertThatThrownBy(() -> tagService.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Tag not found with id: " + id);

            then(tagRepositoryPort).should(never()).deleteById(any());
        }
    }

    // ======================== getTagsAndVerify ========================

    @Nested
    @DisplayName("getTagsAndVerify")
    class GetTagsAndVerify {

        @Test
        @DisplayName("Deve retornar set de tags quando todas existem")
        void shouldReturnTagsWhenAllExist() {
            // given
            Set<UUID> tagIds = Set.of(TagFactory.DEFAULT_ID, TagFactory.SECOND_ID);
            Set<Tag> tags = TagFactory.createDefaultTagSet();

            given(tagRepositoryPort.findAllById(tagIds)).willReturn(tags);

            // when
            Set<Tag> result = tagService.getTagsAndVerify(tagIds);

            // then
            assertThat(result).hasSize(2);
            then(tagRepositoryPort).should().findAllById(tagIds);
        }

        @Test
        @DisplayName("Deve retornar set vazio quando tagIds é null")
        void shouldReturnEmptySetWhenNull() {
            // when
            Set<Tag> result = tagService.getTagsAndVerify(null);

            // then
            assertThat(result).isEmpty();
            then(tagRepositoryPort).should(never()).findAllById(any());
        }

        @Test
        @DisplayName("Deve retornar set vazio quando tagIds está vazio")
        void shouldReturnEmptySetWhenEmpty() {
            // when
            Set<Tag> result = tagService.getTagsAndVerify(Set.of());

            // then
            assertThat(result).isEmpty();
            then(tagRepositoryPort).should(never()).findAllById(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando alguma tag não existe")
        void shouldThrowExceptionWhenSomeTagsNotFound() {
            // given
            UUID existingId = TagFactory.DEFAULT_ID;
            UUID missingId = UUID.randomUUID();
            Set<UUID> tagIds = Set.of(existingId, missingId);

            // Retorna apenas 1 tag, mas pedimos 2
            Set<Tag> foundTags = Set.of(TagFactory.createDefaultTag());
            given(tagRepositoryPort.findAllById(tagIds)).willReturn(foundTags);

            // when / then
            assertThatThrownBy(() -> tagService.getTagsAndVerify(tagIds))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("One or more tags not found");
        }
    }

    // ======================== verifyAllTagsExist ========================

    @Nested
    @DisplayName("verifyAllTagsExist")
    class VerifyAllTagsExist {

        @Test
        @DisplayName("Deve passar silenciosamente quando todas as tags existem")
        void shouldPassWhenAllTagsExist() {
            // given
            Set<UUID> tagIds = Set.of(TagFactory.DEFAULT_ID, TagFactory.SECOND_ID);
            given(tagRepositoryPort.countByIdIn(tagIds)).willReturn(2L);

            // when / then — não deve lançar exceção
            assertThatCode(() -> tagService.verifyAllTagsExist(tagIds))
                    .doesNotThrowAnyException();

            then(tagRepositoryPort).should().countByIdIn(tagIds);
        }

        @Test
        @DisplayName("Deve passar silenciosamente quando tagIds é null")
        void shouldPassWhenNull() {
            // when / then
            assertThatCode(() -> tagService.verifyAllTagsExist(null))
                    .doesNotThrowAnyException();

            then(tagRepositoryPort).should(never()).countByIdIn(any());
        }

        @Test
        @DisplayName("Deve passar silenciosamente quando tagIds está vazio")
        void shouldPassWhenEmpty() {
            // when / then
            assertThatCode(() -> tagService.verifyAllTagsExist(Set.of()))
                    .doesNotThrowAnyException();

            then(tagRepositoryPort).should(never()).countByIdIn(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a contagem não bate")
        void shouldThrowExceptionWhenCountMismatch() {
            // given
            Set<UUID> tagIds = Set.of(TagFactory.DEFAULT_ID, UUID.randomUUID());
            given(tagRepositoryPort.countByIdIn(tagIds)).willReturn(1L); // apenas 1 existe

            // when / then
            assertThatThrownBy(() -> tagService.verifyAllTagsExist(tagIds))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("One or more tags not found");
        }
    }
}
