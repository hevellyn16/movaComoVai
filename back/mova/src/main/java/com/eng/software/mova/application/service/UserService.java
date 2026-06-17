package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserPublicProfileDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.EmailGateway;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.infrastructure.security.Auth0JwtTokenProvider;
import com.eng.software.mova.shared.exceptions.ApiException;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.UserConverter;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder encoder;
    private final Auth0JwtTokenProvider jwtTokenProvider;
    private final EmailGateway emailGateway;
    private final TagService tagService;

    @Value("${app.passwordResetBaseUrl}")
    private String passwordResetBaseUrl;

    @Value("${app.avatar.upload-dir:uploads/avatars}")
    private String avatarUploadDir;

    @Value("${app.avatar.base-url:http://localhost:8080/uploads/avatars}")
    private String avatarBaseUrl;

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserConverter.domainToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return UserConverter.domainToResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepositoryPort.findAll(pageable)
                .map(UserConverter::domainToResponse);
    }

    public UserResponseDTO create(UserCreateDTO dto) {
        if (userRepositoryPort.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("User already exists with email: " + dto.email());
        }

        if (userRepositoryPort.existsByUsername(dto.username())) {
            throw new ResourceAlreadyExistsException("Username already taken: " + dto.username());
        }

        User user = UserConverter.createDTOToDomain(dto);
        user.setPassword(encoder.encode(dto.password()));

        return UserConverter.domainToResponse(userRepositoryPort.save(user));
    }

    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String originalEmail = existing.getEmail();
        String originalUsername = existing.getUsername();

        User updated = UserConverter.updateUserFromDTO(existing, dto);

        if (updated.getEmail() != null && !originalEmail.equals(updated.getEmail()) && userRepositoryPort.existsByEmail(updated.getEmail())) {
            throw new ResourceAlreadyExistsException("User already exists with email: " + updated.getEmail());
        }

        if (updated.getUsername() != null && !originalUsername.equals(updated.getUsername())) {
            if (userRepositoryPort.existsByUsername(updated.getUsername())) {
                throw new ResourceAlreadyExistsException("Username already taken: " + updated.getUsername());
            }
        }

        return UserConverter.domainToResponse(userRepositoryPort.update(updated));
    }

    public UserResponseDTO updateToAdmin(UUID id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setUserType(UserType.ADMIN);
        user.setUpdatedAt(LocalDateTime.now());

        return UserConverter.domainToResponse(userRepositoryPort.update(user));
    }

    public void delete(UUID id) {
        if (!userRepositoryPort.existsById(id)) throw new ResourceNotFoundException("User not found with id: " + id);

        userRepositoryPort.deleteById(id);
    }

    public void forgotPassword(String email) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String token = jwtTokenProvider.generatePasswordResetToken(user.getEmail());
        String resetLink = passwordResetBaseUrl + "/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        emailGateway.sendHtmlEmail(
                user.getEmail(),
                "Recuperacao de senha",
                "reset-password",
                Map.of(
                        "name", user.getName(),
                        "token", token,
                        "resetLink", resetLink
                )
        );
    }

    public void resetPassword(String token, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ApiException("Password and confirm password do not match", HttpStatus.BAD_REQUEST);
        }

        String email = jwtTokenProvider.getEmailFromPasswordResetToken(token);
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setPassword(encoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userRepositoryPort.update(user);
    }

    @Transactional
    public void addTagToUser(UUID id, Set<UUID> tagsId) {
            User user = userRepositoryPort.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            tagService.verifyAllTagsExist(tagsId);

            user.getTagsId().addAll(tagsId);
            user.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.update(user);
    }

    @Transactional
    public void removeTagFromUser(UUID userId, UUID tagId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        tagService.verifyAllTagsExist(Set.of(tagId));

        boolean removed = user.getTagsId().remove(tagId);

        if (removed) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.update(user);
        }
    }

    @Transactional(readOnly = true)
    public UserPublicProfileDTO getPublicProfile(String username) {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (user.isPrivate()) {
            throw new ApiException("This profile is private", HttpStatus.FORBIDDEN);
        }

        return UserConverter.domainToPublicProfile(user);
    }

    public String uploadAvatar(UUID userId, org.springframework.web.multipart.MultipartFile file) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (file.isEmpty()) {
            throw new ApiException("Avatar file must not be empty", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException("Only image files are allowed", HttpStatus.BAD_REQUEST);
        }

        try {
            Path uploadPath = Paths.get(avatarUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar");
            String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
            String filename = userId.toString() + "_" + System.currentTimeMillis() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = avatarBaseUrl + "/" + filename;
            user.setAvatarUrl(avatarUrl);
            user.setUpdatedAt(LocalDateTime.now());
            userRepositoryPort.update(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new ApiException("Failed to upload avatar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

