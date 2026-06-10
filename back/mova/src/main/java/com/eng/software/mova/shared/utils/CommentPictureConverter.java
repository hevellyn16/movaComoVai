package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentPictureEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentPictureConverter {
    public static CommentPicture entityToDomain(CommentPictureEntity commentPictureEntity) {
        return CommentPicture.builder()
                .id(commentPictureEntity.getId())
                .commentId(commentPictureEntity.getComment().getId())
                .pictureUrl(commentPictureEntity.getPictureUrl())
                .build();
    }

    public static CommentPictureEntity domainToEntity(CommentPicture commentPicture) {
        return CommentPictureEntity.builder()
                .id(commentPicture.getId())
                .pictureUrl(commentPicture.getPictureUrl())
                .comment(CommentEntity.builder().id(commentPicture.getCommentId()).build())
                .build();
    }

    public static CommentPictureResponseDTO domainToResponse(CommentPicture commentPicture) {
        return CommentPictureResponseDTO.builder()
                .id(commentPicture.getId())
                .commentId(commentPicture.getCommentId())
                .pictureUrl(commentPicture.getPictureUrl())
                .build();
    }
}

