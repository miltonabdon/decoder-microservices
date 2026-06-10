package com.decoder.authuser.domain.service;

import com.decoder.authuser.adapter.in.controller.dto.UserRequestDto;
import com.decoder.authuser.adapter.in.controller.dto.UserUpdateDto;
import com.decoder.authuser.adapter.out.persistence.RoleRepository;
import com.decoder.authuser.domain.model.*;
import com.decoder.authuser.domain.port.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryPort userRepository;
    private final UserEventPublisherPort eventPublisher;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserModel createUser(UserRequestDto dto) {
        if (userRepository.existsByUsername(dto.username()))
            throw new IllegalArgumentException("Username already taken: " + dto.username());
        if (userRepository.existsByEmail(dto.email()))
            throw new IllegalArgumentException("Email already in use: " + dto.email());

        var user = new UserModel();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFullName(dto.fullName());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUserType(dto.userType() != null ? dto.userType() : UserType.STUDENT);
        user.setActive(true);

        var userType = user.getUserType();
        var roleType = switch (userType) {
            case INSTRUCTOR -> RoleType.ROLE_INSTRUCTOR;
            case ADMIN -> RoleType.ROLE_ADMIN;
            default -> RoleType.ROLE_STUDENT;
        };
        var role = roleRepository.findByRoleType(roleType)
            .orElseThrow(() -> new IllegalStateException("Role not found for type: " + roleType));
        user.setRoles(Set.of(role));

        var saved = userRepository.save(user);
        log.info("User created: {} ({})", saved.getUsername(), saved.getId());
        eventPublisher.publishUserCreated(saved);
        return saved;
    }

    public UserModel findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    public UserModel updateUser(UUID id, UserUpdateDto dto) {
        var user = findById(id);
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        if (dto.email() != null) {
            if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email()))
                throw new IllegalArgumentException("Email already in use: " + dto.email());
            user.setEmail(dto.email());
        }
        var updated = userRepository.save(user);
        eventPublisher.publishUserUpdated(updated);
        return updated;
    }

    @Transactional
    public void deleteUser(UUID id) {
        var user = findById(id);
        userRepository.deleteById(id);
        log.info("User deleted: {} ({})", user.getUsername(), id);
        eventPublisher.publishUserDeleted(user);
    }
}
