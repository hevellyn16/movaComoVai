package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepositoryPort {
    Optional<Comment> findById(UUID id);
    Page<Comment> findByEventId(UUID eventId, Pageable pageable);
    Comment save(Comment comment);
    Comment update(Comment comment);
    void delete(UUID commentId);
}
