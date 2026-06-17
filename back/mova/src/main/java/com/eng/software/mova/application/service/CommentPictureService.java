package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.domain.port.CommentPictureRepositoryPort;
import com.eng.software.mova.domain.port.CommentRepositoryPort;
import com.eng.software.mova.domain.port.FileStoragePort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.CommentPictureConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentPictureService {
    private final CommentPictureRepositoryPort commentPictureRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;
    private final FileStoragePort fileStoragePort;

    public CommentPictureResponseDTO findById(UUID commentId, UUID id) {
        CommentPicture commentPicture = commentPictureRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment picture not found with id: " + id));

        if (!commentId.equals(commentPicture.getCommentId())) {
            throw new ResourceNotFoundException("Comment picture not found with id: " + id);
        }

        return CommentPictureConverter.domainToResponse(commentPicture);
    }

    public Page<CommentPictureResponseDTO> findByCommentId(UUID commentId, Pageable pageable) {
        Page<CommentPicture> commentPictures = commentPictureRepositoryPort.findByCommentId(commentId, pageable);
        return commentPictures.map(CommentPictureConverter::domainToResponse);
    }

    @Transactional
    public void deleteById(UUID commentId, UUID id) {
        CommentPicture commentPicture = commentPictureRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment picture not found with id: " + id));

        if (!commentId.equals(commentPicture.getCommentId())) {
            throw new ResourceNotFoundException("Comment picture not found with id: " + id);
        }

        commentPictureRepositoryPort.deleteById(id);
    }

    @Transactional
    public CommentPictureResponseDTO savePicture(UUID commentId, MultipartFile file) {
        String imageUrl = fileStoragePort.uploadFile(file);

        Comment comment = commentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        CommentPicture picture = CommentPicture.builder()
                .commentId(comment.getId())
                .pictureUrl(imageUrl)
                .build();

        return CommentPictureConverter.domainToResponse(commentPictureRepositoryPort.save(picture));
    }
}


