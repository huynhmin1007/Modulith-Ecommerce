package com.dev.minn.ecommerce.identity.entity;

import com.dev.minn.ecommerce.common.domain.BaseEntity;
import com.dev.minn.ecommerce.identity.entity.associate.RolePermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity<UUID> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    UUID id;

    // service:entity:action:scope
    @Column(unique = true, nullable = false, length = 100)
    String name;

    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "permission")
    @Builder.Default
    Set<RolePermission> roles = new HashSet<>();
}
