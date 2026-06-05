package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.enums.UserType;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceAlreadyExistsException;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepositoryPort userRepositoryPort;
//    private final PasswordEncoder encoder;

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
//        user.setPassword(encoder.encode(dto.password()));

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
        userRepositoryPort.deleteById(id);
    }
}

