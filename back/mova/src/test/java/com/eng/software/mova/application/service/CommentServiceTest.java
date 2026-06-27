package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.port.CommentRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepositoryPort commentRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void shouldReturnCommentResponseDTO_whenFindByIdExists() {
        // Given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Ótimo evento")
                .userId(userId)
                .eventId(eventId)
                .createdAt(createdAt)
                .build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));

        CommentResponseDTO response = commentService.findById(commentId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(commentId);
        assertThat(response.content()).isEqualTo("Ótimo evento");
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.eventId()).isEqualTo(eventId);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldThrowNotFoundException_whenFindByIdDoesNotExist() {
        UUID commentId = UUID.randomUUID();
        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found!");

        verify(commentRepositoryPort, times(1)).findById(commentId);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldReturnPageOfCommentResponseDTO_whenFindAllByEventId() {
        UUID eventId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Ótimo evento")
                .userId(userId)
                .eventId(eventId)
                .createdAt(LocalDateTime.now())
                .build();

        Page<Comment> page = new PageImpl<>(List.of(comment), PageRequest.of(0, 10), 1);
        when(commentRepositoryPort.findByEventId(eventId, PageRequest.of(0, 10))).thenReturn(page);

        Page<CommentResponseDTO> responsePage = commentService.findAllByEventId(eventId, PageRequest.of(0, 10));

        assertThat(responsePage.getTotalElements()).isEqualTo(1);
        assertThat(responsePage.getContent()).hasSize(1);
        assertThat(responsePage.getContent().getFirst().id()).isEqualTo(commentId);
        verify(commentRepositoryPort, times(1)).findByEventId(eventId, PageRequest.of(0, 10));
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldUpdateComment_whenAuthorMatches() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        CommentCreateDTO updateDTO = new CommentCreateDTO("Atualizado");

        Comment existing = Comment.builder()
                .id(commentId)
                .content("Antigo")
                .userId(userId)
                .eventId(eventId)
                .createdAt(LocalDateTime.now().minusDays(1))
                .likedByUsers(Set.of())
                .build();

        Comment updated = Comment.builder()
                .id(commentId)
                .content("Atualizado")
                .userId(userId)
                .eventId(eventId)
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .likedByUsers(Set.of())
                .build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepositoryPort.save(existing)).thenReturn(updated);

        CommentResponseDTO response = commentService.update(commentId, updateDTO, userId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(commentId);
        assertThat(response.content()).isEqualTo("Atualizado");
        assertThat(response.userId()).isEqualTo(userId);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentRepositoryPort, times(1)).save(existing);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldThrowWhenUserDoesNotMatch_onUpdate() {
        UUID commentId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Comment existing = Comment.builder()
                .id(commentId)
                .content("Antigo")
                .userId(ownerId)
                .eventId(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(1))
                .likedByUsers(Set.of())
                .build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> commentService.update(commentId, new CommentCreateDTO("Nova"), otherUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found for this user!");

        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentRepositoryPort, never()).save(any());
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldLikeComment_whenCommentAndUserExist() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Comentário")
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .likedByUsers(new java.util.HashSet<>())
                .build();

        User user = User.builder().id(userId).build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepositoryPort.save(comment)).thenReturn(comment);

        commentService.likeComment(commentId, userId);

        assertThat(comment.getLikedByUsers()).contains(userId);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(commentRepositoryPort, times(1)).save(comment);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldUnlikeComment_whenCommentAndUserExist() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Comentário")
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .likedByUsers(new java.util.HashSet<>(Set.of(userId)))
                .build();

        User user = User.builder().id(userId).build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepositoryPort.save(comment)).thenReturn(comment);

        commentService.unlikeComment(commentId, userId);

        assertThat(comment.getLikedByUsers()).doesNotContain(userId);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(commentRepositoryPort, times(1)).save(comment);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldDeleteComment_whenDeleteIsCalled() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Comment comment = Comment.builder().id(commentId).userId(userId).build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(commentId, userId);

        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentRepositoryPort, times(1)).delete(commentId);
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldThrowWhenUserDoesNotMatch_onDelete() {
        UUID commentId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Comment comment = Comment.builder().id(commentId).userId(ownerId).build();

        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(commentId, otherUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found for this user!");

        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentRepositoryPort, never()).delete(any());
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }

    @Test
    public void shouldThrowWhenCommentOrUserIsMissing_onLike() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.likeComment(commentId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found with id " + commentId);

        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(userRepositoryPort, never()).findById(any());
        verify(commentRepositoryPort, never()).save(any());
        verifyNoMoreInteractions(commentRepositoryPort, userRepositoryPort);
    }
}

