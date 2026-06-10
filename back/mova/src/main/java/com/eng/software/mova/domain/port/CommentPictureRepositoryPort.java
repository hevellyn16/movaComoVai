package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.CommentPicture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CommentPictureRepositoryPort {
    Optional<CommentPicture> findById(UUID id);
    Set<CommentPicture> findByCommentId(UUID commentId);
    CommentPicture save(CommentPicture commentPicture);
    void deleteById(UUID id);
}

