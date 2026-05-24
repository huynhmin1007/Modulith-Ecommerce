package com.dev.minn.ecommerce.identity.entity;

import com.dev.minn.ecommerce.common.domain.entity.SoftDeleteEntity;
import com.dev.minn.ecommerce.identity.constant.UserStatus;
import com.dev.minn.ecommerce.identity.entity.associate.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        schema = "identity",
        name = "users"
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends SoftDeleteEntity<UUID> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Builder.Default
    @Column(name = "status", nullable = false)
    UserStatus status = UserStatus.INACTIVE;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    Set<UserRole> roles = new HashSet<>();

    public Set<Role> getRolesAsRole() {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void addRole(Role role) {
        UserRole userRole = UserRole.builder()
                .user(this)
                .role(role)
                .build();
        this.roles.add(userRole);
    }

    public void removeRole(Role role) {
        roles.removeIf(ur -> ur.getRole().getId().equals(role.getId()));
    }
}