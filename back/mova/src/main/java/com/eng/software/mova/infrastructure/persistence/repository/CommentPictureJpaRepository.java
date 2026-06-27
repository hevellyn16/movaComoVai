package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.CommentPictureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface CommentPictureJpaRepository extends JpaRepository<CommentPictureEntity, UUID> {
    Page<CommentPictureEntity> findByCommentId(UUID commentId, Pageable pageable);
}

