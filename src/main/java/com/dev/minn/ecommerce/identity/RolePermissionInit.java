package com.dev.minn.ecommerce.identity;

import com.dev.minn.ecommerce.identity.entity.Permission;
import com.dev.minn.ecommerce.identity.entity.Role;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RolePermissionInit {

    EntityManager entityManager;

    @Transactional
    public void initRolesAndPermissions() {

        Long roleCount = entityManager
                .createQuery("SELECT COUNT(r) FROM Role r", Long.class)
                .getSingleResult();

        if (roleCount > 0) {
            log.info("Roles & permissions đã tồn tại, bỏ qua init.");
            return;
        }

        log.info("Bắt đầu khởi tạo PBAC roles & permissions...");

        // =========================================================
        // SYSTEM
        // =========================================================

        Permission pSystemAll = createPermission(
                "*:*:*:any",
                "Toàn quyền hệ thống"
        );

        // =========================================================
        // IDENTITY
        // =========================================================

        Permission pUserReadOwn = createPermission(
                "identity:user:read:own",
                "Xem thông tin tài khoản cá nhân"
        );

        Permission pUserWriteOwn = createPermission(
                "identity:user:update:own",
                "Cập nhật tài khoản cá nhân"
        );

        Permission pUserReadAny = createPermission(
                "identity:user:read:any",
                "Xem thông tin user khác"
        );

        // =========================================================
        // CATALOG
        // =========================================================

        Permission pProductReadAny = createPermission(
                "catalog:product:read:any",
                "Xem mọi sản phẩm"
        );

        Permission pProductCreateOwn = createPermission(
                "catalog:product:create:own",
                "Tạo sản phẩm của shop mình"
        );

        Permission pProductUpdateOwn = createPermission(
                "catalog:product:update:own",
                "Cập nhật sản phẩm của shop mình"
        );

        Permission pProductDeleteOwn = createPermission(
                "catalog:product:delete:own",
                "Xóa sản phẩm của shop mình"
        );

        // =========================================================
        // SALES
        // =========================================================

        Permission pOrderCreateOwn = createPermission(
                "sales:order:create:own",
                "Tạo đơn hàng"
        );

        Permission pOrderReadOwn = createPermission(
                "sales:order:read:own",
                "Xem đơn hàng của mình"
        );

        Permission pOrderUpdateOwn = createPermission(
                "sales:order:update:own",
                "Cập nhật đơn hàng của mình"
        );

        // =========================================================
        // PAYMENT
        // =========================================================

        Permission pPaymentReadOwn = createPermission(
                "finance:payment:read:own",
                "Xem lịch sử thanh toán cá nhân"
        );

        Permission pPaymentCreateOwn = createPermission(
                "finance:payment:create:own",
                "Thanh toán"
        );

        // =========================================================
        // STATISTICS
        // =========================================================

        Permission pStatisticsReadOwn = createPermission(
                "finance:statistics:read:own",
                "Xem thống kê shop"
        );

        // =========================================================
        // ROLES
        // =========================================================

        // ---------------------------------------------------------
        // CUSTOMER
        // ---------------------------------------------------------

        Role customerRole = createRole("CUSTOMER");

        customerRole.addPermission(pUserReadOwn);
        customerRole.addPermission(pUserWriteOwn);

        customerRole.addPermission(pProductReadAny);

        customerRole.addPermission(pOrderCreateOwn);
        customerRole.addPermission(pOrderReadOwn);

        customerRole.addPermission(pPaymentReadOwn);
        customerRole.addPermission(pPaymentCreateOwn);

        entityManager.persist(customerRole);

        // ---------------------------------------------------------
        // SELLER
        // ---------------------------------------------------------

        Role sellerRole = createRole("SELLER");

        // user
        sellerRole.addPermission(pUserReadOwn);
        sellerRole.addPermission(pUserWriteOwn);
        sellerRole.addPermission(pUserReadAny);

        // product
        sellerRole.addPermission(pProductReadAny);
        sellerRole.addPermission(pProductCreateOwn);
        sellerRole.addPermission(pProductUpdateOwn);
        sellerRole.addPermission(pProductDeleteOwn);

        // order
        sellerRole.addPermission(pOrderReadOwn);
        sellerRole.addPermission(pOrderUpdateOwn);

        // payment
        sellerRole.addPermission(pPaymentReadOwn);

        // statistics
        sellerRole.addPermission(pStatisticsReadOwn);

        entityManager.persist(sellerRole);

        // ---------------------------------------------------------
        // ADMIN
        // ---------------------------------------------------------

        Role adminRole = createRole("ADMIN");

        adminRole.addPermission(pSystemAll);

        entityManager.persist(adminRole);

        log.info("Khởi tạo PBAC roles & permissions thành công!");
    }

    private Permission createPermission(String name, String description) {

        Permission permission = Permission.builder()
                .name(name)
                .description(description)
                .build();

        entityManager.persist(permission);

        return permission;
    }

    private Role createRole(String name) {

        return Role.builder()
                .name(name)
                .build();
    }
}