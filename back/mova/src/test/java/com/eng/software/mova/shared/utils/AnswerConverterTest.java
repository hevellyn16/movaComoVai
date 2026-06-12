package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.infrastructure.persistence.entity.AnswerEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class AnswerConverterTest {

    @Test
    public void shouldMapEntityToDomain_whenEntityIsValid() {
        // Given
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        AnswerEntity entity = AnswerEntity.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .author(UserEntity.builder().id(authorId).build())
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        Answer domain = AnswerConverter.entityToDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(answerId);
        assertThat(domain.getAnswer()).isEqualTo("Resposta");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(domain.getAuthorId()).isEqualTo(authorId);
        assertThat(domain.getCommentId()).isEqualTo(commentId);
    }

    @Test
    public void shouldMapDomainToEntity_whenDomainIsValid() {
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LocalDateTime updatedAt = LocalDateTime.now();

        Answer answer = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .authorId(authorId)
                .commentId(commentId)
                .build();

        AnswerEntity entity = AnswerConverter.domainToEntity(answer);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(answerId);
        assertThat(entity.getAnswer()).isEqualTo("Resposta");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(entity.getAuthor()).isNotNull();
        assertThat(entity.getAuthor().getId()).isEqualTo(authorId);
        assertThat(entity.getComment()).isNotNull();
        assertThat(entity.getComment().getId()).isEqualTo(commentId);
    }

    @Test
    public void shouldMapDomainToResponseDTO_whenDomainIsValid() {
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(30);
        LocalDateTime updatedAt = LocalDateTime.now();

        Answer answer = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .authorId(authorId)
                .commentId(commentId)
                .build();

        AnswerResponseDTO dto = AnswerConverter.domainToResponseDTO(answer);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(answerId);
        assertThat(dto.answer()).isEqualTo("Resposta");
        assertThat(dto.createdAt()).isEqualTo(createdAt);
        assertThat(dto.updatedAt()).isEqualTo(updatedAt);
        assertThat(dto.userId()).isEqualTo(authorId);
        assertThat(dto.commentId()).isEqualTo(commentId);
    }

    @Test
    public void shouldCreateDomainFromCreateDTO_whenInputIsValid() {
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        AnswerCreateDTO createDTO = new AnswerCreateDTO("Nova resposta");

        Answer answer = AnswerConverter.createDTOToDomain(createDTO, authorId, commentId);

        assertThat(answer).isNotNull();
        assertThat(answer.getAnswer()).isEqualTo("Nova resposta");
        assertThat(answer.getAuthorId()).isEqualTo(authorId);
        assertThat(answer.getCommentId()).isEqualTo(commentId);
        assertThat(answer.getCreatedAt()).isNotNull();
    }

    @Test
    public void shouldUpdateAnswer_whenUpdateDTOIsValid() {
        Answer answer = Answer.builder()
                .id(UUID.randomUUID())
                .answer("Antiga resposta")
                .authorId(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .build();
        AnswerCreateDTO updateDTO = new AnswerCreateDTO("Resposta atualizada");

        AnswerConverter.updateAnswer(answer, updateDTO);

        assertThat(answer.getAnswer()).isEqualTo("Resposta atualizada");
        assertThat(answer.getUpdatedAt()).isNotNull();
    }

    @Test
    public void shouldHandleNullInputs_whenNullIsPassed() {
        assertThat(AnswerConverter.entityToDomain(null)).isNull();
        assertThat(AnswerConverter.domainToEntity(null)).isNull();
        assertThat(AnswerConverter.domainToResponseDTO(null)).isNull();
        assertThat(AnswerConverter.createDTOToDomain(null, UUID.randomUUID(), UUID.randomUUID())).isNull();
        assertThatCode(() -> AnswerConverter.updateAnswer(null, null)).doesNotThrowAnyException();
        assertThatCode(() -> AnswerConverter.updateAnswer(Answer.builder().build(), null)).doesNotThrowAnyException();
    }
}

