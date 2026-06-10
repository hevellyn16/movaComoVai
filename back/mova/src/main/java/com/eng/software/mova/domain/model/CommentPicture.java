package com.eng.software.mova.domain.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommentPicture {
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID commentId;
    private String pictureUrl;
}

