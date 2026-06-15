package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.picture.CommentPictureResponseDTO;
import com.eng.software.mova.domain.model.CommentPicture;
import com.eng.software.mova.infrastructure.persistence.entity.CommentEntity;
import com.eng.software.mova.infrastructure.persistence.entity.CommentPictureEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentPictureConverterTest {

    @Test
    public void shouldMapEntityToDomain_whenEntityIsValid() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPictureEntity entity = CommentPictureEntity.builder()
                .id(id)
                .pictureUrl(url)
                .comment(CommentEntity.builder().id(commentId).build())
                .build();

        CommentPicture domain = CommentPictureConverter.entityToDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getCommentId()).isEqualTo(commentId);
        assertThat(domain.getPictureUrl()).isEqualTo(url);
    }

    @Test
    public void shouldMapDomainToEntity_whenDomainIsValid() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPicture domain = CommentPicture.builder()
                .id(id)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        CommentPictureEntity entity = CommentPictureConverter.domainToEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getPictureUrl()).isEqualTo(url);
        assertThat(entity.getComment()).isNotNull();
        assertThat(entity.getComment().getId()).isEqualTo(commentId);
    }

    @Test
    public void shouldMapDomainToResponse_whenDomainIsValid() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPicture domain = CommentPicture.builder()
                .id(id)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        CommentPictureResponseDTO response = CommentPictureConverter.domainToResponse(domain);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.commentId()).isEqualTo(commentId);
        assertThat(response.pictureUrl()).isEqualTo(url);
    }
}
