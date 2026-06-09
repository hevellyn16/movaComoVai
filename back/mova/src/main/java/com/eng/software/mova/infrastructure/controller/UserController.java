package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.tag.UserTagAssociationDTO;
import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.application.dto.auth.EmailRequestDTO;
import com.eng.software.mova.application.dto.auth.PasswordRequestDTO;
import com.eng.software.mova.application.service.UserService;
import com.eng.software.mova.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO userCreated = userService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userCreated.id())
                .toUri();

        return ResponseEntity.created(location).body(userCreated);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody EmailRequestDTO dto) {
        userService.forgotPassword(dto.email());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token,
                                              @Valid @RequestBody PasswordRequestDTO dto) {
        userService.resetPassword(token, dto.password(), dto.confirmPassword());
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateDTO dto,
                                                  @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        if (authenticatedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID id = authenticatedUser.getId();
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateToAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.updateToAdmin(id));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        if (authenticatedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID id = authenticatedUser.getId();
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/tags")
    public ResponseEntity<Void> addTagToUser(@RequestBody UserTagAssociationDTO dto,
                                             @RequestAttribute String userId
    ) {
        userService.addTagToUser(UUID.fromString(userId), dto.tagIds());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromUser(
            @PathVariable UUID tagId,
            @RequestAttribute String userId) {

        userService.removeTagFromUser(UUID.fromString(userId), tagId);
        return ResponseEntity.noContent().build();
    }
}

