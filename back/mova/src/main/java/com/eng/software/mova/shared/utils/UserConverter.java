package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
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
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .userType(userEntity.getUserType())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .isActive(userEntity.isActive())
                .tagsId(userEntity.getTags() != null ?
                        userEntity.getTags().stream().map(TagEntity::getId).collect(Collectors.toSet())
                        : new HashSet<>())
                .build();
    }

    public static UserEntity domainToEntity(User user) {
        if (user == null) return null;

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .userType(user.getUserType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.isActive())
                .tags(user.getTagsId() != null ? user.getTagsId().stream().map(
                        tagId -> TagEntity.builder().id(tagId).build()
                ).collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }

    public static UserResponseDTO domainToResponse(User user) {
        if (user == null) return null;

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
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
                .email(dto.email())
                .password(dto.password())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userType(UserType.COMMON)
                .isActive(true)
                .build();
    }

    public static User updateUserFromDTO(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) return null;

        user.setName(dto.name() != null ? dto.name() : user.getName());
        user.setEmail(dto.email() != null ? dto.email() : user.getEmail());
        user.setPassword(dto.password() != null ? dto.password() : user.getPassword());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }
}
