package com.eng.software.mova.domain.model;

import com.eng.software.mova.domain.model.enums.UserType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private UUID id;
    private String name;
    private String email;
    private String password;
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private String username;
    private String avatarUrl;
    private String bio;
    private String location;
    private boolean isPrivate;
    private boolean pushNotifications;
    private boolean emailNotifications;
    private Set<UUID> tagsId;
    private Set<UUID> likedEvents;
    private Set<UUID> favoriteEvents;
}
