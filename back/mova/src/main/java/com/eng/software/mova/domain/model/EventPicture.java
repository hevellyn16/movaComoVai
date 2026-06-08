package com.eng.software.mova.domain.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventPicture {
    @EqualsAndHashCode.Include
    private UUID id;
    private String pictureUrl;
}
