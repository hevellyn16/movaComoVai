package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.domain.port.CommentPictureRepositoryPort;
import com.eng.software.mova.domain.port.CommentRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentPictureServiceTest {

    @Mock
    private CommentPictureRepositoryPort commentPictureRepositoryPort;

    @Mock
    private CommentRepositoryPort commentRepositoryPort;

    @Mock
    private FileStoragePort fileStoragePort;

    @InjectMocks
    private CommentPictureService commentPictureService;

    // ─── findById ───────────────────────────────────────────────────────────

    @Test
    public void shouldReturnCommentPictureResponseDTO_whenFindByIdExists() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPicture picture = CommentPicture.builder()
                .id(pictureId)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        CommentPictureResponseDTO response = commentPictureService.findById(commentId, pictureId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(pictureId);
        assertThat(response.commentId()).isEqualTo(commentId);
        assertThat(response.pictureUrl()).isEqualTo(url);
        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenFindByIdDoesNotExist() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentPictureService.findById(commentId, pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment picture not found with id: " + pictureId);

        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenFindByIdCommentIdDoesNotMatch() {
        UUID commentId = UUID.randomUUID();
        UUID otherCommentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();

        CommentPicture picture = CommentPicture.builder()
                .id(pictureId)
                .commentId(otherCommentId)
                .pictureUrl("url")
                .build();

        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        assertThatThrownBy(() -> commentPictureService.findById(commentId, pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment picture not found with id: " + pictureId);

        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    // ─── findByCommentId ────────────────────────────────────────────────────

    @Test
    public void shouldReturnPageOfResponseDTO_whenFindByCommentId() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";
        PageRequest pageable = PageRequest.of(0, 10);

        CommentPicture picture = CommentPicture.builder()
                .id(pictureId)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        Page<CommentPicture> page = new PageImpl<>(List.of(picture), pageable, 1);
        when(commentPictureRepositoryPort.findByCommentId(commentId, pageable)).thenReturn(page);

        Page<CommentPictureResponseDTO> result = commentPictureService.findByCommentId(commentId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(pictureId);
        assertThat(result.getContent().getFirst().commentId()).isEqualTo(commentId);
        verify(commentPictureRepositoryPort, times(1)).findByCommentId(commentId, pageable);
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    // ─── deleteById ─────────────────────────────────────────────────────────

    @Test
    public void shouldDelete_whenDeleteByIdIsCalledWithMatchingCommentId() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();

        CommentPicture picture = CommentPicture.builder()
                .id(pictureId)
                .commentId(commentId)
                .pictureUrl("url")
                .build();

        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        commentPictureService.deleteById(commentId, pictureId);

        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verify(commentPictureRepositoryPort, times(1)).deleteById(pictureId);
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenDeleteByIdDoesNotExist() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentPictureService.deleteById(commentId, pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment picture not found with id: " + pictureId);

        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verify(commentPictureRepositoryPort, never()).deleteById(any());
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenDeleteByIdCommentIdDoesNotMatch() {
        UUID commentId = UUID.randomUUID();
        UUID otherCommentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();

        CommentPicture picture = CommentPicture.builder()
                .id(pictureId)
                .commentId(otherCommentId)
                .pictureUrl("url")
                .build();

        when(commentPictureRepositoryPort.findById(pictureId)).thenReturn(Optional.of(picture));

        assertThatThrownBy(() -> commentPictureService.deleteById(commentId, pictureId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment picture not found with id: " + pictureId);

        verify(commentPictureRepositoryPort, times(1)).findById(pictureId);
        verify(commentPictureRepositoryPort, never()).deleteById(any());
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    // ─── savePicture ────────────────────────────────────────────────────────

    @Test
    public void shouldSaveAndReturnResponseDTO_whenSavePictureIsCalledWithValidInput() {
        UUID commentId = UUID.randomUUID();
        UUID pictureId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "picture.jpg", "image/jpeg", "fake-content".getBytes());

        Comment comment = Comment.builder()
                .id(commentId)
                .content("Comentário")
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .build();

        CommentPicture savedPicture = CommentPicture.builder()
                .id(pictureId)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        when(fileStoragePort.uploadFile(file)).thenReturn(url);
        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentPictureRepositoryPort.save(any(CommentPicture.class))).thenReturn(savedPicture);

        CommentPictureResponseDTO response = commentPictureService.savePicture(commentId, file);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(pictureId);
        assertThat(response.commentId()).isEqualTo(commentId);
        assertThat(response.pictureUrl()).isEqualTo(url);
        verify(fileStoragePort, times(1)).uploadFile(file);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentPictureRepositoryPort, times(1)).save(any(CommentPicture.class));
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }

    @Test
    public void shouldThrowNotFoundException_whenSavePictureCommentNotFound() {
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "picture.jpg", "image/jpeg", "fake-content".getBytes());

        when(fileStoragePort.uploadFile(file)).thenReturn(url);
        when(commentRepositoryPort.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentPictureService.savePicture(commentId, file))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found with id: " + commentId);

        verify(fileStoragePort, times(1)).uploadFile(file);
        verify(commentRepositoryPort, times(1)).findById(commentId);
        verify(commentPictureRepositoryPort, never()).save(any());
        verifyNoMoreInteractions(commentPictureRepositoryPort, commentRepositoryPort, fileStoragePort);
    }
}
