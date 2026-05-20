package com.dev.minn.ecommerce.identity;

import com.dev.minn.ecommerce.identity.constant.UserStatus;
import com.dev.minn.ecommerce.identity.entity.Role;
import com.dev.minn.ecommerce.identity.entity.User;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserInit {

    EntityManager entityManager;
    PasswordEncoder passwordEncoder;

    static int TOTAL_FAKE_USERS = 1000;
    static int BATCH_SIZE = 100;

    @Transactional
    public void initUsers() {
        Long userCount = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        if (userCount > 0) {
            log.info("Users đã tồn tại, bỏ qua Init.");
            return;
        }

        log.info("Bắt đầu khởi tạo Users...");
        Faker faker = new Faker();

        // 1. Truy vấn các Role từ Database (do class RolePermissionInitService vừa tạo xong)
        Role adminRole = getRoleByName("ADMIN");
        Role customerRole = getRoleByName("CUSTOMER");

        // 2. Tạo 3 User cố định
        entityManager.persist(createUser("admin", "admin@ecommerce.com", hashPassword("admin"), adminRole));
        entityManager.persist(createUser("minh", "21130445@st.hcmuaf.edu.vn", hashPassword("123456"), customerRole));

        entityManager.flush();
        entityManager.clear(); // Xả RAM ngay

        // 3. Tạo User ngẫu nhiên theo Batch
        log.info("Tạo {} random users theo batch...", TOTAL_FAKE_USERS);

        // Lấy ID để dùng Proxy, cực kỳ tối ưu cho vòng lặp 10,000
        UUID customerRoleId = customerRole.getId();

        Set<String> generatedEmails = new HashSet<>();

        for (int i = 1; i <= TOTAL_FAKE_USERS; i++) {
            String randomUsername = "user" + i;
            String randomEmail;

            do {
                randomEmail = faker.internet().emailAddress();
            } while (generatedEmails.contains(randomEmail) || isEmailExistsInDb(randomEmail));

            generatedEmails.add(randomEmail);

            User randomUser = User.builder()
                    .username(randomUsername)
                    .email(randomEmail)
                    .password(hashPassword("123456"))
                    .status(UserStatus.ACTIVE)
                    .build();

            // Gắn khóa ngoại qua Proxy để tránh Detached Entity
            Role proxyCustomerRole = entityManager.getReference(Role.class, customerRoleId);
            randomUser.addRole(proxyCustomerRole);

            entityManager.persist(randomUser);

            if (i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();
        log.info("Hoàn tất tạo mồi Users!");
    }

    private Role getRoleByName(String name) {
        return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    private User createUser(String username, String email, String encodedPassword, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .status(UserStatus.ACTIVE)
                .build();
        user.addRole(role);
        return user;
    }

    private boolean isEmailExistsInDb(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}