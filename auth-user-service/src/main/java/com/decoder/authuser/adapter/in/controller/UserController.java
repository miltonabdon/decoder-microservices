package com.decoder.authuser.adapter.in.controller;

import com.decoder.authuser.adapter.in.controller.dto.*;
import com.decoder.authuser.adapter.in.security.JwtProvider;
import com.decoder.authuser.domain.model.UserModel;
import com.decoder.authuser.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<UserModel> createUser(@RequestBody @Valid UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        var auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        var user = (UserModel) auth.getPrincipal();
        var token = jwtProvider.generateToken(user);
        return ResponseEntity.ok(new JwtResponseDto(token, user.getId(), user.getUsername(), user.getEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserModel>> listUsers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(null, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
