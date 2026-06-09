package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    Optional<User> findByEmailOrUsername(String email, String username);
    Page<User> findAll(Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsById(UUID id);
    User save(User user);
    User update(User user);
    void deleteById(UUID id);
}
