package com.decoder.authuser.adapter.out.persistence;

import com.decoder.authuser.domain.model.RoleModel;
import com.decoder.authuser.domain.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    Optional<RoleModel> findByRoleType(RoleType roleType);
}
