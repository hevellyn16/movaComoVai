package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
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
public class CommentRepositoryTest {

    @Mock
    private CommentJpaRepository commentJpaRepository;

    @InjectMocks
    private CommentRepository commentRepository;

    @Test
    public void shouldReturnMappedComment_whenFindByIdExists() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID likedUserId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        CommentEntity entity = CommentEntity.builder()
                .id(commentId)
                .comment("Ótimo evento")
                .createdAt(createdAt)
                .user(UserEntity.builder().id(userId).build())
                .event(EventEntity.builder().id(eventId).build())
                .likedByUsers(java.util.Set.of(UserEntity.builder().id(likedUserId).build()))
                .build();

        when(commentJpaRepository.findById(commentId)).thenReturn(Optional.of(entity));

        Optional<Comment> result = commentRepository.findById(commentId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(commentId);
        assertThat(result.get().getContent()).isEqualTo("Ótimo evento");
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getEventId()).isEqualTo(eventId);
        assertThat(result.get().getLikedByUsers()).containsExactly(likedUserId);
        verify(commentJpaRepository, times(1)).findById(commentId);
        verifyNoMoreInteractions(commentJpaRepository);
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindByIdDoesNotExist() {
        UUID commentId = UUID.randomUUID();
        when(commentJpaRepository.findById(commentId)).thenReturn(Optional.empty());

        Optional<Comment> result = commentRepository.findById(commentId);

        assertThat(result).isEmpty();
        verify(commentJpaRepository, times(1)).findById(commentId);
        verifyNoMoreInteractions(commentJpaRepository);
    }

    @Test
    public void shouldReturnMappedPage_whenFindByEventId() {
        UUID eventId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID likedUserId = UUID.randomUUID();

        CommentEntity entity = CommentEntity.builder()
                .id(commentId)
                .comment("Ótimo evento")
                .createdAt(LocalDateTime.now())
                .user(UserEntity.builder().id(userId).build())
                .event(EventEntity.builder().id(eventId).build())
                .likedByUsers(java.util.Set.of(UserEntity.builder().id(likedUserId).build()))
                .build();

        var page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
        when(commentJpaRepository.findByEventId(eventId, PageRequest.of(0, 10))).thenReturn(page);

        var result = commentRepository.findByEventId(eventId, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(commentId);
        assertThat(result.getContent().getFirst().getEventId()).isEqualTo(eventId);
        verify(commentJpaRepository, times(1)).findByEventId(eventId, PageRequest.of(0, 10));
        verifyNoMoreInteractions(commentJpaRepository);
    }

    @Test
    public void shouldSaveAndReturnMappedComment_whenSaveIsCalled() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID likedUserId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);

        Comment domain = Comment.builder()
                .id(commentId)
                .content("Comentário")
                .userId(userId)
                .eventId(eventId)
                .createdAt(createdAt)
                .likedByUsers(new java.util.HashSet<>(List.of(likedUserId)))
                .build();

        CommentEntity savedEntity = CommentEntity.builder()
                .id(commentId)
                .comment("Comentário")
                .createdAt(createdAt)
                .user(UserEntity.builder().id(userId).build())
                .event(EventEntity.builder().id(eventId).build())
                .likedByUsers(java.util.Set.of(UserEntity.builder().id(likedUserId).build()))
                .build();

        when(commentJpaRepository.save(any(CommentEntity.class))).thenReturn(savedEntity);

        Comment result = commentRepository.save(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getLikedByUsers()).containsExactly(likedUserId);
        verify(commentJpaRepository, times(1)).save(any(CommentEntity.class));
        verifyNoMoreInteractions(commentJpaRepository);
    }

    @Test
    public void shouldUpdateAndReturnMappedComment_whenUpdateIsCalled() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Comment domain = Comment.builder()
                .id(commentId)
                .content("Comentário atualizado")
                .userId(userId)
                .eventId(eventId)
                .updatedAt(LocalDateTime.now())
                .build();

        CommentEntity savedEntity = CommentEntity.builder()
                .id(commentId)
                .comment("Comentário atualizado")
                .user(UserEntity.builder().id(userId).build())
                .event(EventEntity.builder().id(eventId).build())
                .build();

        when(commentJpaRepository.save(any(CommentEntity.class))).thenReturn(savedEntity);

        Comment result = commentRepository.update(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getContent()).isEqualTo("Comentário atualizado");
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEventId()).isEqualTo(eventId);
        verify(commentJpaRepository, times(1)).save(any(CommentEntity.class));
        verifyNoMoreInteractions(commentJpaRepository);
    }

    @Test
    public void shouldDeleteComment_whenDeleteIsCalled() {
        UUID commentId = UUID.randomUUID();
        CommentEntity reference = CommentEntity.builder().id(commentId).build();
        when(commentJpaRepository.getReferenceById(commentId)).thenReturn(reference);

        commentRepository.delete(commentId);

        verify(commentJpaRepository, times(1)).getReferenceById(commentId);
        verify(commentJpaRepository, times(1)).delete(reference);
        verifyNoMoreInteractions(commentJpaRepository);
    }
}


