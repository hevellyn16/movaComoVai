package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserPublicProfileDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.infrastructure.persistence.entity.TagEntity;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    public static User entityToDomain(UserEntity userEntity) {
        if (userEntity == null) return null;

        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .avatarUrl(userEntity.getAvatarUrl())
                .bio(userEntity.getBio())
                .location(userEntity.getLocation())
                .isPrivate(userEntity.isPrivate())
                .pushNotifications(userEntity.isPushNotifications())
                .emailNotifications(userEntity.isEmailNotifications())
                .userType(userEntity.getUserType())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .isActive(userEntity.isActive())
                .tagsId(userEntity.getTags() != null ?
                        userEntity.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet())
                        : new HashSet<>())
                .favoriteEvents(userEntity.getFavoriteEvents() != null ?
                        userEntity.getFavoriteEvents().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : new HashSet<>())
                .build();
    }

    public static UserEntity domainToEntity(User user) {
        if (user == null) return null;

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .isPrivate(user.isPrivate())
                .pushNotifications(user.isPushNotifications())
                .emailNotifications(user.isEmailNotifications())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.isActive())
                .tags(user.getTagsId() != null ? user.getTagsId().stream().map(
                        tagId -> TagEntity.builder().id(tagId).build()
                ).collect(Collectors.toSet()) : new HashSet<>())
                .favoriteEvents(user.getFavoriteEvents() != null ?
                        user.getFavoriteEvents().stream().map(eventId -> com.eng.software.mova.infrastructure.persistence.entity.EventEntity.builder().id(eventId).build()).collect(Collectors.toSet())
                        : new HashSet<>())
                .build();
    }

    public static UserResponseDTO domainToResponse(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .isPrivate(user.isPrivate())
                .userType(user.getUserType().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.isActive())
                .build();
    }

    public static User createDTOToDomain(UserCreateDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .name(dto.name())
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userType(UserType.COMMON)
                .isActive(true)
                .isPrivate(false)
                .pushNotifications(true)
                .emailNotifications(true)
                .build();
    }

    public static User updateUserFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) return null;

        if (dto.name() != null) user.setName(dto.name());
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(dto.password());
        if (dto.avatarUrl() != null) user.setAvatarUrl(dto.avatarUrl());
        if (dto.bio() != null) user.setBio(dto.bio());
        if (dto.location() != null) user.setLocation(dto.location());
        if (dto.isPrivate() != null) user.setPrivate(dto.isPrivate());
        if (dto.pushNotifications() != null) user.setPushNotifications(dto.pushNotifications());
        if (dto.emailNotifications() != null) user.setEmailNotifications(dto.emailNotifications());

        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    public static UserPublicProfileDTO domainToPublicProfile(User user) {
        if (user == null) return null;

        return UserPublicProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
