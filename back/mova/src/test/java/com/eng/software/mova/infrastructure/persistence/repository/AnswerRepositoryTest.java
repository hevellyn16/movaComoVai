package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.infrastructure.persistence.entity.AnswerEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnswerRepositoryTest {

    @Mock
    private AnswerJpaRepository answerJpaRepository;

    @InjectMocks
    private AnswerRepository answerRepository;

    @Test
    public void shouldReturnMappedAnswer_whenFindByIdExists() {
        // Given
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        AnswerEntity entity = AnswerEntity.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(createdAt)
                .author(UserEntity.builder().id(authorId).build())
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        when(answerJpaRepository.findById(answerId)).thenReturn(Optional.of(entity));

        Optional<Answer> result = answerRepository.findById(answerId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(answerId);
        assertThat(result.get().getAnswer()).isEqualTo("Resposta");
        assertThat(result.get().getAuthorId()).isEqualTo(authorId);
        assertThat(result.get().getCommentId()).isEqualTo(commentId);
        verify(answerJpaRepository, times(1)).findById(answerId);
        verifyNoMoreInteractions(answerJpaRepository);
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindByIdDoesNotExist() {
        UUID answerId = UUID.randomUUID();
        when(answerJpaRepository.findById(answerId)).thenReturn(Optional.empty());

        Optional<Answer> result = answerRepository.findById(answerId);

        assertThat(result).isEmpty();
        verify(answerJpaRepository, times(1)).findById(answerId);
        verifyNoMoreInteractions(answerJpaRepository);
    }

    @Test
    public void shouldReturnMappedPage_whenFindByCommentId() {
        UUID commentId = UUID.randomUUID();
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        AnswerEntity entity = AnswerEntity.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(LocalDateTime.now())
                .author(UserEntity.builder().id(authorId).build())
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        var page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
        when(answerJpaRepository.findByCommentId(commentId, PageRequest.of(0, 10))).thenReturn(page);

        var result = answerRepository.findByCommentId(commentId, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(answerId);
        assertThat(result.getContent().getFirst().getCommentId()).isEqualTo(commentId);
        verify(answerJpaRepository, times(1)).findByCommentId(commentId, PageRequest.of(0, 10));
        verifyNoMoreInteractions(answerJpaRepository);
    }

    @Test
    public void shouldSaveAndReturnMappedAnswer_whenSaveIsCalled() {
        // Given
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);

        Answer domain = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(createdAt)
                .build();

        AnswerEntity savedEntity = AnswerEntity.builder()
                .id(answerId)
                .answer("Resposta")
                .createdAt(createdAt)
                .author(UserEntity.builder().id(authorId).build())
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        when(answerJpaRepository.save(any(AnswerEntity.class))).thenReturn(savedEntity);

        Answer result = answerRepository.save(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(answerId);
        assertThat(result.getAuthorId()).isEqualTo(authorId);
        assertThat(result.getCommentId()).isEqualTo(commentId);
        verify(answerJpaRepository, times(1)).save(any(AnswerEntity.class));
        verifyNoMoreInteractions(answerJpaRepository);
    }

    @Test
    public void shouldUpdateAndReturnMappedAnswer_whenUpdateIsCalled() {
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        Answer domain = Answer.builder()
                .id(answerId)
                .answer("Resposta atualizada")
                .authorId(authorId)
                .commentId(commentId)
                .updatedAt(LocalDateTime.now())
                .build();

        AnswerEntity savedEntity = AnswerEntity.builder()
                .id(answerId)
                .answer("Resposta atualizada")
                .author(UserEntity.builder().id(authorId).build())
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        when(answerJpaRepository.save(any(AnswerEntity.class))).thenReturn(savedEntity);

        Answer result = answerRepository.update(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(answerId);
        assertThat(result.getAnswer()).isEqualTo("Resposta atualizada");
        assertThat(result.getAuthorId()).isEqualTo(authorId);
        assertThat(result.getCommentId()).isEqualTo(commentId);
        verify(answerJpaRepository, times(1)).save(any(AnswerEntity.class));
        verifyNoMoreInteractions(answerJpaRepository);
    }

    @Test
    public void shouldDeleteById_whenDeleteIsCalled() {
        UUID answerId = UUID.randomUUID();

        answerRepository.delete(answerId);

        verify(answerJpaRepository, times(1)).deleteById(answerId);
        verifyNoMoreInteractions(answerJpaRepository);
    }
}



