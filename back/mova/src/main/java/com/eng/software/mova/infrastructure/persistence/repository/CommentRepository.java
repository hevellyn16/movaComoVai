package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.domain.port.CommentRepositoryPort;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.shared.utils.CommentConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentRepository implements CommentRepositoryPort {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Optional<Comment> findById(UUID id) {
        return commentJpaRepository.findById(id).map(CommentConverter::entityToDomain);
    }

    @Override
    public Page<Comment> findByEventId(UUID eventId, Pageable pageable) {
        return commentJpaRepository.findByEventId(eventId, pageable).map(CommentConverter::entityToDomain);
    }

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = CommentConverter.domainToEntity(comment);
        return CommentConverter.entityToDomain(commentJpaRepository.save(entity));
    }

    @Override
    public Comment update(Comment comment) {
        CommentEntity entity = CommentConverter.domainToEntity(comment);
        return CommentConverter.entityToDomain(commentJpaRepository.save(entity));
    }

    @Override
    public void delete(UUID commentId) {
        CommentEntity entity = commentJpaRepository.getReferenceById(commentId);
        commentJpaRepository.delete(entity);
    }
}
