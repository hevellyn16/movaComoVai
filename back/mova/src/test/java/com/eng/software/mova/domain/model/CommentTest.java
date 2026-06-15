package com.eng.software.mova.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @Test
    public void shouldInitializeDefaultFields_whenBuildingCommentWithoutOptionalValues() {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content("Ótimo evento")
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .build();

        assertThat(comment.getCreatedAt()).isNotNull();
        assertThat(comment.getUpdatedAt()).isNull();
        assertThat(comment.getLikedByUsers()).isNotNull().isEmpty();
    }

    @Test
    public void shouldPreserveExplicitValues_whenBuildingCommentWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        UUID likedUserId = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .content("Conteúdo")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .likedByUsers(Set.of(likedUserId))
                .build();

        assertThat(comment.getCreatedAt()).isEqualTo(createdAt);
        assertThat(comment.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(comment.getLikedByUsers()).containsExactly(likedUserId);
    }
}

