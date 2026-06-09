package com.decoder.authuser.adapter.out.persistence;

import com.decoder.authuser.domain.model.UserModel;
import com.decoder.authuser.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;

    @Override public UserModel save(UserModel u) { return jpaRepository.save(u); }
    @Override public Optional<UserModel> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public Optional<UserModel> findByUsername(String u) { return jpaRepository.findByUsername(u); }
    @Override public Optional<UserModel> findByEmail(String e) { return jpaRepository.findByEmail(e); }
    @Override public boolean existsByUsername(String u) { return jpaRepository.existsByUsername(u); }
    @Override public boolean existsByEmail(String e) { return jpaRepository.existsByEmail(e); }
    @Override public Page<UserModel> findAll(Specification<UserModel> s, Pageable p) { return jpaRepository.findAll(s, p); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}
