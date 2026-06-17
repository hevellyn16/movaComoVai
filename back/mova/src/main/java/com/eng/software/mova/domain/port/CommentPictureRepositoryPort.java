package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.CommentPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CommentPictureRepositoryPort {
    Optional<CommentPicture> findById(UUID id);
    Page<CommentPicture> findByCommentId(UUID commentId, Pageable pageable);
    CommentPicture save(CommentPicture commentPicture);
    void deleteById(UUID id);
}

