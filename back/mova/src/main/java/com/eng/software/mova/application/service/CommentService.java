package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.port.CommentRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.CommentConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepositoryPort commentRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public CommentResponseDTO findById(UUID id) {
        Comment comment = commentRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found!"));

        return CommentConverter.domainToResponseDTO(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> findAllByEventId(UUID eventId, Pageable pageable) {
        Page<Comment> comments = commentRepositoryPort.findByEventId(eventId, pageable);
        return comments.map(CommentConverter::domainToResponseDTO);
    }

    public CommentResponseDTO create(CommentCreateDTO createDTO, UUID userId, UUID eventId) {
        Comment comment = CommentConverter.createDTOToDomain(createDTO, userId, eventId);
        comment =  commentRepositoryPort.save(comment);
        return CommentConverter.domainToResponseDTO(comment);
    }

    public CommentResponseDTO update(UUID commentId, CommentCreateDTO updateDTO, UUID userId) {
        Comment comment = commentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found!"));

        if (comment.getUserId() != null && !comment.getUserId().equals(userId))
            throw new ResourceNotFoundException("Comment not found for this user!");

        CommentConverter.updateComment(comment, updateDTO);

        comment = commentRepositoryPort.save(comment);
        return CommentConverter.domainToResponseDTO(comment);
    }

    public void delete(UUID commentId) {
        commentRepositoryPort.delete(commentId);
    }

    @Transactional
    public void likeComment(UUID commentId, UUID userId) {
        Comment comment = commentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        comment.getLikedByUsers().add(userId);
        commentRepositoryPort.save(comment);
    }

    @Transactional
    public void unlikeComment(UUID commentId, UUID userId) {
        Comment comment = commentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        comment.getLikedByUsers().remove(userId);
        commentRepositoryPort.save(comment);
    }
}
