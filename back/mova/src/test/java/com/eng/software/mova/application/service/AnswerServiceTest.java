package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.infrastructure.persistence.repository.AnswerRepository;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @Test
    public void shouldReturnAnswerResponseDTO_whenFindByIdExists() {
        // Given
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Answer answer = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(createdAt)
                .build();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

        AnswerResponseDTO response = answerService.findById(answerId);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(answerId);
        assertThat(response.answer()).isEqualTo("Resposta");
        assertThat(response.userId()).isEqualTo(authorId);
        assertThat(response.commentId()).isEqualTo(commentId);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        verify(answerRepository, times(1)).findById(answerId);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldThrowNotFoundException_whenFindByIdDoesNotExist() {
        UUID answerId = UUID.randomUUID();
        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.findById(answerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Answer not found with id: " + answerId);

        verify(answerRepository, times(1)).findById(answerId);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldReturnPageOfAnswerResponseDTO_whenFindByCommentId() {
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID answerId = UUID.randomUUID();

        Answer answer = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now())
                .build();

        Page<Answer> page = new PageImpl<>(List.of(answer), PageRequest.of(0, 10), 1);
        when(answerRepository.findByCommentId(commentId, PageRequest.of(0, 10))).thenReturn(page);

        Page<AnswerResponseDTO> responsePage = answerService.findByCommentId(commentId, PageRequest.of(0, 10));

        assertThat(responsePage.getTotalElements()).isEqualTo(1);
        assertThat(responsePage.getContent()).hasSize(1);
        assertThat(responsePage.getContent().get(0).id()).isEqualTo(answerId);
        assertThat(responsePage.getContent().get(0).commentId()).isEqualTo(commentId);
        verify(answerRepository, times(1)).findByCommentId(commentId, PageRequest.of(0, 10));
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldCreateAnswer_whenInputIsValid() {
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID answerId = UUID.randomUUID();
        AnswerCreateDTO dto = new AnswerCreateDTO("Nova resposta");

        Answer saved = Answer.builder()
                .id(answerId)
                .answer("Nova resposta")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now())
                .build();

        when(answerRepository.save(any(Answer.class))).thenReturn(saved);

        AnswerResponseDTO response = answerService.create(dto, authorId, commentId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(answerId);
        assertThat(response.answer()).isEqualTo("Nova resposta");
        assertThat(response.userId()).isEqualTo(authorId);
        assertThat(response.commentId()).isEqualTo(commentId);
        verify(answerRepository, times(1)).save(any(Answer.class));
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldUpdateAnswer_whenAuthorMatches() {
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        AnswerCreateDTO updateDTO = new AnswerCreateDTO("Resposta atualizada");

        Answer existing = Answer.builder()
                .id(answerId)
                .answer("Resposta antiga")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Answer updated = Answer.builder()
                .id(answerId)
                .answer("Resposta atualizada")
                .authorId(authorId)
                .commentId(commentId)
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existing));
        when(answerRepository.save(existing)).thenReturn(updated);

        AnswerResponseDTO response = answerService.update(updateDTO, answerId, authorId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(answerId);
        assertThat(response.answer()).isEqualTo("Resposta atualizada");
        assertThat(response.userId()).isEqualTo(authorId);
        verify(answerRepository, times(1)).findById(answerId);
        verify(answerRepository, times(1)).save(existing);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldThrowWhenAuthorDoesNotMatch_onUpdate() {
        UUID answerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherAuthorId = UUID.randomUUID();
        Answer existing = Answer.builder()
                .id(answerId)
                .answer("Resposta antiga")
                .authorId(ownerId)
                .commentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> answerService.update(new AnswerCreateDTO("Nova"), answerId, otherAuthorId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Answer not found for this author");

        verify(answerRepository, times(1)).findById(answerId);
        verify(answerRepository, never()).save(any());
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldDeleteAnswer_whenAuthorMatches() {
        UUID answerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Answer existing = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .authorId(authorId)
                .commentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existing));

        answerService.deleteById(answerId, authorId);

        verify(answerRepository, times(1)).findById(answerId);
        verify(answerRepository, times(1)).delete(answerId);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    public void shouldThrowWhenAuthorDoesNotMatch_onDelete() {
        UUID answerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherAuthorId = UUID.randomUUID();

        Answer existing = Answer.builder()
                .id(answerId)
                .answer("Resposta")
                .authorId(ownerId)
                .commentId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> answerService.deleteById(answerId, otherAuthorId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Answer not found for this author");

        verify(answerRepository, times(1)).findById(answerId);
        verify(answerRepository, never()).delete(any());
        verifyNoMoreInteractions(answerRepository);
    }
}

