package com.eng.software.mova.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {

    @Test
    public void shouldInitializeCreatedAt_whenNotProvided() {
        Answer answer = Answer.builder()
                .id(UUID.randomUUID())
                .answer("Resposta")
                .authorId(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .build();

        assertThat(answer.getCreatedAt()).isNotNull();
        assertThat(answer.getUpdatedAt()).isNull();
    }

    @Test
    public void shouldPreserveExplicitTimestamps_whenProvidedInBuilder() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Answer answer = Answer.builder()
                .id(UUID.randomUUID())
                .answer("Resposta")
                .authorId(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertThat(answer.getCreatedAt()).isEqualTo(createdAt);
        assertThat(answer.getUpdatedAt()).isEqualTo(updatedAt);
    }
}

