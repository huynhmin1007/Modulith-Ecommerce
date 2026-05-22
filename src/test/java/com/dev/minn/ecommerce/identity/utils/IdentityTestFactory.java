package com.dev.minn.ecommerce.identity.utils;

import com.dev.minn.ecommerce.identity.entity.Permission;
import com.dev.minn.ecommerce.identity.entity.Role;
import com.dev.minn.ecommerce.identity.entity.User;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

public final class IdentityTestFactory {

    private IdentityTestFactory() {
    }

    public static User admin() {

        Permission permission = Permission.builder()
                .name("*:*:*:*")
                .build();

        Role role = Role.builder()
                .name("ADMIN")
                .build();

        role.addPermission(permission);

        User admin = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .email("admin@example.com")
                .password("Password123@")
                .build();

        admin.addRole(role);

        return admin;
    }

    public static User customer(String email, String password, Set<String> permissions) {

        Role role = Role.builder()
                .name("CUSTOMER")
                .build();

        if(!CollectionUtils.isEmpty(permissions)){
            for (String perm : permissions) {
                if (perm.equals("*:*:*:*")) {
                    throw new IllegalArgumentException("Customer cannot have wildcard permissions");
                }

                role.addPermission(Permission.builder().name(perm).build());
            }
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .email(email)
                .password(password)
                .build();

        user.addRole(role);

        return user;
    }

    public static User customer(Set<String> permissions) {
        return customer("customer@example.com", "Password123@", permissions);
    }
}