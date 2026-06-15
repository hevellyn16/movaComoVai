package com.eng.software.mova.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentPictureTest {

    @Test
    public void shouldBuildCommentPictureWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        String url = "https://storage.example.com/picture.jpg";

        CommentPicture commentPicture = CommentPicture.builder()
                .id(id)
                .commentId(commentId)
                .pictureUrl(url)
                .build();

        assertThat(commentPicture.getId()).isEqualTo(id);
        assertThat(commentPicture.getCommentId()).isEqualTo(commentId);
        assertThat(commentPicture.getPictureUrl()).isEqualTo(url);
    }

    @Test
    public void shouldBuildCommentPictureWithNullOptionalFields() {
        CommentPicture commentPicture = CommentPicture.builder()
                .id(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .build();

        assertThat(commentPicture.getPictureUrl()).isNull();
    }

    @Test
    public void shouldRespectEquality_whenSameId() {
        UUID id = UUID.randomUUID();

        CommentPicture p1 = CommentPicture.builder()
                .id(id)
                .commentId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        CommentPicture p2 = CommentPicture.builder()
                .id(id)
                .commentId(UUID.randomUUID())
                .pictureUrl("url-b")
                .build();

        assertThat(p1).isEqualTo(p2);
    }

    @Test
    public void shouldNotBeEqual_whenDifferentId() {
        CommentPicture p1 = CommentPicture.builder()
                .id(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        CommentPicture p2 = CommentPicture.builder()
                .id(UUID.randomUUID())
                .commentId(UUID.randomUUID())
                .pictureUrl("url-a")
                .build();

        assertThat(p1).isNotEqualTo(p2);
    }
}
