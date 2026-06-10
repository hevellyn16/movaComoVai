package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.comment.CommentCreateDTO;
import com.eng.software.mova.application.dto.comment.CommentResponseDTO;
import com.eng.software.mova.domain.model.Comment;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CommentConverter {

    public static Comment entityToDomain(CommentEntity entity) {
        if (entity == null) return null;

        Set<UUID> likedByUserIds = entity.getLikedByUsers() != null
                ? entity.getLikedByUsers().stream().map(UserEntity::getId).collect(Collectors.toSet())
                : new java.util.HashSet<>();

        return Comment.builder()
                .id(entity.getId())
                .content(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .eventId(entity.getEvent() != null ? entity.getEvent().getId() : null)
                .likedByUsers(likedByUserIds)
                .build();
    }

    public static CommentEntity domainToEntity(Comment domain) {
        if (domain == null) return null;
        CommentEntity entity = new CommentEntity();
        entity.setId(domain.getId());
        entity.setComment(domain.getContent());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getUserId() != null) {
            entity.setUser(UserEntity.builder().id(domain.getUserId()).build());
        }

        if (domain.getEventId() != null) {
            entity.setEvent(EventEntity.builder().id(domain.getEventId()).build());
        }

        if (domain.getLikedByUsers() != null && !domain.getLikedByUsers().isEmpty()) {
            Set<UserEntity> likedByUsers = domain.getLikedByUsers().stream()
                    .map(userId -> UserEntity.builder().id(userId).build())
                    .collect(Collectors.toSet());
            entity.setLikedByUsers(likedByUsers);
        } else {
            entity.setLikedByUsers(new java.util.HashSet<>());
        }

        return entity;
    }

    public static CommentResponseDTO domainToResponseDTO(Comment comment) {
        if (comment == null) return null;

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .eventId(comment.getEventId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static Comment createDTOToDomain(CommentCreateDTO createDTO, UUID userId, UUID eventId) {
        return Comment.builder()
                .content(createDTO.content())
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .eventId(eventId)
                .build();
    }
    public static void updateComment(Comment comment, CommentCreateDTO createDTO) {
        comment.setContent(createDTO.content());
        comment.setUpdatedAt(LocalDateTime.now());
    }
}
