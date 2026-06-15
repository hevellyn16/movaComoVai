package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentPictureEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentPictureRepositoryTest {

    @Mock
    private CommentPictureJpaRepository commentPictureJpaRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private CommentPictureRepository commentPictureRepository;

    @Test
    public void shouldReturnMappedCommentPicture_whenFindByIdExists() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPictureEntity entity = CommentPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        when(commentPictureJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<CommentPicture> result = commentPictureRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCommentId()).isEqualTo(commentId);
        assertThat(result.get().getPictureUrl()).isEqualTo(url);
        verify(commentPictureJpaRepository, times(1)).findById(id);
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }

    @Test
    public void shouldReturnEmptyOptional_whenFindByIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(commentPictureJpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CommentPicture> result = commentPictureRepository.findById(id);

        assertThat(result).isEmpty();
        verify(commentPictureJpaRepository, times(1)).findById(id);
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }

    @Test
    public void shouldReturnMappedPage_whenFindByCommentId() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPictureEntity entity = CommentPictureEntity.builder()
                .id(pictureId)
                .pictureUrl(url)
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<CommentPictureEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(commentPictureJpaRepository.findByCommentId(commentId, pageable)).thenReturn(entityPage);

        Page<CommentPicture> result = commentPictureRepository.findByCommentId(commentId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(pictureId);
        assertThat(result.getContent().getFirst().getCommentId()).isEqualTo(commentId);
        verify(commentPictureJpaRepository, times(1)).findByCommentId(commentId, pageable);
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }

    @Test
    public void shouldSaveAndReturnMappedCommentPicture_whenSaveIsCalled() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPicture domain = CommentPicture.builder()
                .id(id)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        CommentPictureEntity savedEntity = CommentPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        when(commentPictureJpaRepository.save(any(CommentPictureEntity.class))).thenReturn(savedEntity);

        CommentPicture result = commentPictureRepository.save(domain);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCommentId()).isEqualTo(commentId);
        assertThat(result.getPictureUrl()).isEqualTo(url);
        verify(commentPictureJpaRepository, times(1)).save(any(CommentPictureEntity.class));
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }

    @Test
    public void shouldDeleteCommentPictureAndFile_whenDeleteByIdIsCalled() {
        UUID id = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPictureEntity entity = CommentPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .comment(CommentEntity.builder().id(UUID.randomUUID()).build())
                .build();

        when(commentPictureJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        commentPictureRepository.deleteById(id);

        verify(commentPictureJpaRepository, times(1)).findById(id);
        verify(commentPictureJpaRepository, times(1)).delete(entity);
        verify(fileStoragePort, times(1)).deleteFile(url);
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }

    @Test
    public void shouldThrowException_whenDeleteByIdEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(commentPictureJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentPictureRepository.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Comment picture not found with id: " + id);

        verify(commentPictureJpaRepository, times(1)).findById(id);
        verify(commentPictureJpaRepository, never()).delete(any());
        verify(fileStoragePort, never()).deleteFile(any());
        verifyNoMoreInteractions(commentPictureJpaRepository, fileStoragePort);
    }
}
