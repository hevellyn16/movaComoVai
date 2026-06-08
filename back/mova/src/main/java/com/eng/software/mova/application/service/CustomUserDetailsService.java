package com.eng.software.mova.application.service;

import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = repository.findByEmailOrUsername(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getName())
                .password(user.getPassword())
                .authorities(user.getUserType().name())
                .email(user.getEmail())
                .build();
    }
}
