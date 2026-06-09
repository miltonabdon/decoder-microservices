package com.decoder.authuser.domain.port;

import com.decoder.authuser.domain.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    UserModel save(UserModel user);
    Optional<UserModel> findById(UUID id);
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);
    void deleteById(UUID id);
}
