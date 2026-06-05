package com.eng.software.mova.infrastructure.controller;

import com.eng.software.mova.application.dto.user.UserCreateDTO;
import com.eng.software.mova.application.dto.user.UserResponseDTO;
import com.eng.software.mova.application.dto.user.UserUpdateDTO;
import com.eng.software.mova.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PutMapping("/{id}/admin")
    public ResponseEntity<UserResponseDTO> updateToAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.updateToAdmin(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}

