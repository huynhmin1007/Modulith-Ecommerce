package com.dev.minn.ecommerce.identity.repository;

import com.dev.minn.ecommerce.identity.entity.Role;
import com.dev.minn.ecommerce.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);
}
