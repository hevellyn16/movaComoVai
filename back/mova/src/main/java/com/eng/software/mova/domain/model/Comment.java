package com.eng.software.mova.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @EqualsAndHashCode.Include
    private UUID id;
    private String content;
    @Builder.Default
    private LocalDateTime createdAt =  LocalDateTime.now();
    private LocalDateTime updatedAt;
    private UUID userId;
    private UUID eventId;
    @Builder.Default
    private Set<UUID> likedByUsers = new HashSet<>();
}
