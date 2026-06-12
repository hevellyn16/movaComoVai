package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CommentConverterTest {

    @Test
    public void shouldMapEntityToDomain_whenEntityIsValid() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID likedUserId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        CommentEntity entity = CommentEntity.builder()
                .id(commentId)
                .comment("Ótimo evento")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .user(UserEntity.builder().id(userId).build())
                .event(EventEntity.builder().id(eventId).build())
                .likedByUsers(Set.of(UserEntity.builder().id(likedUserId).build()))
                .build();

        Comment domain = CommentConverter.entityToDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(commentId);
        assertThat(domain.getContent()).isEqualTo("Ótimo evento");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getEventId()).isEqualTo(eventId);
        assertThat(domain.getLikedByUsers()).containsExactly(likedUserId);
    }

    @Test
    public void shouldMapDomainToEntity_whenDomainIsValid() {
        // Given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID likedUserId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LocalDateTime updatedAt = LocalDateTime.now();

        Comment domain = Comment.builder()
                .id(commentId)
                .content("Ótimo evento")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .userId(userId)
                .eventId(eventId)
                .likedByUsers(Set.of(likedUserId))
                .build();

        CommentEntity entity = CommentConverter.domainToEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(commentId);
        assertThat(entity.getComment()).isEqualTo("Ótimo evento");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(entity.getUser()).isNotNull();
        assertThat(entity.getUser().getId()).isEqualTo(userId);
        assertThat(entity.getEvent()).isNotNull();
        assertThat(entity.getEvent().getId()).isEqualTo(eventId);
        assertThat(entity.getLikedByUsers()).hasSize(1);
        assertThat(entity.getLikedByUsers().iterator().next().getId()).isEqualTo(likedUserId);
    }

    @Test
    public void shouldMapDomainToResponseDTO_whenDomainIsValid() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(30);
        LocalDateTime updatedAt = LocalDateTime.now();

        Comment domain = Comment.builder()
                .id(commentId)
                .content("Ótimo evento")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .userId(userId)
                .eventId(eventId)
                .build();

        CommentResponseDTO dto = CommentConverter.domainToResponseDTO(domain);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(commentId);
        assertThat(dto.content()).isEqualTo("Ótimo evento");
        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.eventId()).isEqualTo(eventId);
        assertThat(dto.createdAt()).isEqualTo(createdAt);
        assertThat(dto.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    public void shouldCreateDomainFromCreateDTO_whenInputIsValid() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        CommentCreateDTO createDTO = new CommentCreateDTO("Comentário novo");

        Comment comment = CommentConverter.createDTOToDomain(createDTO, userId, eventId);

        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isEqualTo("Comentário novo");
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getEventId()).isEqualTo(eventId);
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    public void shouldUpdateComment_whenUpdateDTOIsValid() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content("Antigo")
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .build();
        CommentCreateDTO updateDTO = new CommentCreateDTO("Atualizado");

        CommentConverter.updateComment(comment, updateDTO);

        assertThat(comment.getContent()).isEqualTo("Atualizado");
        assertThat(comment.getUpdatedAt()).isNotNull();
    }

    @Test
    public void shouldHandleNullInputs_whenNullIsPassed() {
        assertThat(CommentConverter.entityToDomain(null)).isNull();
        assertThat(CommentConverter.domainToEntity(null)).isNull();
        assertThat(CommentConverter.domainToResponseDTO(null)).isNull();
        assertThat(CommentConverter.createDTOToDomain(null, UUID.randomUUID(), UUID.randomUUID())).isNull();
        assertThatCode(() -> CommentConverter.updateComment(null, null)).doesNotThrowAnyException();
        assertThatCode(() -> CommentConverter.updateComment(Comment.builder().build(), null)).doesNotThrowAnyException();
    }
}

