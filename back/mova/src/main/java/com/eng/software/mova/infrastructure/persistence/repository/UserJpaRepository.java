package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("UPDATE UserEntity u SET u.isActive = false WHERE u.id = :id")
    @Modifying
    void deleteById(UUID id);
}
