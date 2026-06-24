package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.answer.AnswerCreateDTO;
import com.eng.software.mova.application.dto.answer.AnswerResponseDTO;
import com.eng.software.mova.domain.model.Answer;
import com.eng.software.mova.infrastructure.persistence.entity.AnswerEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AnswerConverter {
    public static Answer entityToDomain(AnswerEntity entity) {
        if (entity == null) return null;

        return Answer.builder()
                .id(entity.getId())
                .answer(entity.getAnswer())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .authorId(entity.getAuthor() != null ? entity.getAuthor().getId() : null)
                .authorName(entity.getAuthor() != null ? entity.getAuthor().getName() : null)
                .authorAvatarUrl(entity.getAuthor() != null ? entity.getAuthor().getAvatarUrl() : null)
                .commentId(entity.getComment() != null ? entity.getComment().getId() : null)
                .build();
    }

    public static AnswerEntity domainToEntity(Answer answer) {
        if (answer == null) return null;

        return AnswerEntity.builder()
                .id(answer.getId())
                .answer(answer.getAnswer())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .author(UserEntity.builder().id(answer.getAuthorId()).build())
                .comment(CommentEntity.builder().id(answer.getCommentId()).build())
                .build();
    }

    public static AnswerResponseDTO domainToResponseDTO(Answer answer) {
        if (answer == null) return null;

        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .answer(answer.getAnswer())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .userId(answer.getAuthorId())
                .userName(answer.getAuthorName())
                .userAvatarUrl(answer.getAuthorAvatarUrl())
                .commentId(answer.getCommentId())
                .build();
    }

    public static Answer createDTOToDomain(AnswerCreateDTO answer, UUID authorId, UUID commentId) {
        if (answer == null) return null;

        return Answer.builder()
                .answer(answer.answer())
                .createdAt(LocalDateTime.now())
                .authorId(authorId)
                .commentId(commentId)
                .build();
    }

    public static void updateAnswer(Answer answer, AnswerCreateDTO createDTO) {
        if (answer == null || createDTO == null) return;

        answer.setAnswer(createDTO.answer());
        answer.setUpdatedAt(LocalDateTime.now());
    }
}
