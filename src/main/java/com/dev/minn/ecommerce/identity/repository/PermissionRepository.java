package com.dev.minn.ecommerce.identity.repository;

import com.dev.minn.ecommerce.identity.entity.Permission;
import com.dev.minn.ecommerce.identity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

}
