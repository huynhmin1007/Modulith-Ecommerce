package com.dev.minn.ecommerce.identity;

import com.dev.minn.ecommerce.identity.constant.UserStatus;
import com.dev.minn.ecommerce.identity.entity.Role;
import com.dev.minn.ecommerce.identity.entity.User;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInit {

    EntityManager entityManager;
    PasswordEncoder passwordEncoder;

    static int TOTAL_FAKE_USERS = 1000;

    @Transactional
    public void initUsers() {

        Long userCount = entityManager
                .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();

        if (userCount > 0) {
            log.info("Users đã tồn tại, bỏ qua init.");
            return;
        }

        log.info("Bắt đầu khởi tạo users...");

        // Lấy role một lần duy nhất
        Role adminRole = getRoleByName("ADMIN");
        Role customerRole = getRoleByName("CUSTOMER");

        // Encode password một lần duy nhất
        String adminPassword = passwordEncoder.encode("Admin123@");
        String defaultUserPassword = passwordEncoder.encode("Password123@");

        // =========================
        // Admin user
        // =========================
        User admin = User.builder()
                .username("admin")
                .email("admin@ecommerce.com")
                .password(adminPassword)
                .status(UserStatus.ACTIVE)
                .build();

        admin.addRole(adminRole);

        entityManager.persist(admin);

        // =========================
        // Fake users
        // =========================
        log.info("Tạo {} fake users...", TOTAL_FAKE_USERS);

        for (int i = 1; i <= TOTAL_FAKE_USERS; i++) {

            User user = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@ecommerce.com")
                    .password(defaultUserPassword)
                    .status(UserStatus.ACTIVE)
                    .build();

            // Dùng luôn managed entity
            user.addRole(customerRole);

            entityManager.persist(user);
        }

        log.info("Hoàn tất tạo users!");
    }

    private Role getRoleByName(String name) {
        return entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.name = :name",
                        Role.class
                )
                .setParameter("name", name)
                .getSingleResult();
    }
}