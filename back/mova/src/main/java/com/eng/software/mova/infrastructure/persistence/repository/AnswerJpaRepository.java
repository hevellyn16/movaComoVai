package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, UUID> {
    Page<AnswerEntity> findByCommentId(UUID commentId, Pageable pageable);
}
