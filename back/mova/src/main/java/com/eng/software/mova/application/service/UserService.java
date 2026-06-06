package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder encoder;
    private final Auth0JwtTokenProvider jwtTokenProvider;
    private final EmailGateway emailGateway;

    @Value("${app.passwordResetBaseUrl}")
    private String passwordResetBaseUrl;

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

        User user = UserConverter.createDTOToDomain(dto);
        user.setPassword(encoder.encode(dto.password()));

        return UserConverter.domainToResponse(userRepositoryPort.save(user));
    }

    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        User existing = userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User updated = UserConverter.updateUserFromDTO(existing, dto);

        if (!existing.getEmail().equals(updated.getEmail()) && userRepositoryPort.existsByEmail(updated.getEmail())) {
            throw new ResourceAlreadyExistsException("User already exists with email: " + updated.getEmail());
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
}

