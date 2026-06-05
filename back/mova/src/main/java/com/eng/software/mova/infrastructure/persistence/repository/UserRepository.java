package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import com.eng.software.mova.shared.utils.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> user = userJpaRepository.findByEmail(email);

        return user.map(UserConverter::entityToDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Optional<UserEntity> user = userJpaRepository.findById(id);

        return user.map(UserConverter::entityToDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable)
                .map(UserConverter::entityToDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return UserConverter.entityToDomain(userJpaRepository.save(UserConverter.domainToEntity(user)));
    }

    @Override
    public User update(User user) {
        return UserConverter.entityToDomain(userJpaRepository.save(UserConverter.domainToEntity(user)));
    }

    @Override
    public void deleteById(UUID id) {
        if (!userJpaRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userJpaRepository.deleteById(id);
    }
}
