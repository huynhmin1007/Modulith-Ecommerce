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
        Long roleCount = entityManager.createQuery("SELECT COUNT(r) FROM Role r", Long.class).getSingleResult();
        if (roleCount > 0) {
            log.info("Roles và Permissions đã tồn tại, bỏ qua Init.");
            return;
        }

        log.info("Bắt đầu khởi tạo Roles và Permissions (PBAC)...");

        // 1. TẠO PERMISSIONS
        Permission pAll = createPermission("*:*:*", "Toàn quyền hệ thống");
        Permission pProductRead = createPermission("catalog:product:read", "Xem sản phẩm");
        Permission pProductWrite = createPermission("catalog:product:write", "Quản lý sản phẩm");
        Permission pOrderRead = createPermission("sales:order:read", "Xem đơn hàng");
        Permission pOrderWrite = createPermission("sales:order:write", "Tạo và xử lý đơn hàng");
        Permission pPaymentRead = createPermission("finance:payment:read", "Xem giao dịch");
        Permission pPaymentWrite = createPermission("finance:payment:write", "Thanh toán, rút tiền");
        Permission pStatsRead = createPermission("finance:statistics:read", "Xem thống kê doanh thu");
        Permission pProfileRead = createPermission("user:profile:read", "Xem thông tin user");
        Permission pProfileWrite = createPermission("user:profile:write", "Sửa thông tin cá nhân");

        // 2. TẠO ROLES
        Role roleCustomer = createRole("CUSTOMER");
        roleCustomer.addPermission(pProductRead);
        roleCustomer.addPermission(pOrderRead);
        roleCustomer.addPermission(pOrderWrite);
        roleCustomer.addPermission(pPaymentRead);
        roleCustomer.addPermission(pProfileRead);
        roleCustomer.addPermission(pProfileWrite);
        entityManager.persist(roleCustomer);

        Role roleSeller = createRole("SELLER");
        roleSeller.addPermission(pProductRead);
        roleSeller.addPermission(pProductWrite);
        roleSeller.addPermission(pOrderRead);
        roleSeller.addPermission(pOrderWrite);
        roleSeller.addPermission(pPaymentRead);
        roleSeller.addPermission(pPaymentWrite);
        roleSeller.addPermission(pStatsRead);
        roleSeller.addPermission(pProfileRead);
        roleSeller.addPermission(pProfileWrite);
        entityManager.persist(roleSeller);

        Role roleAdmin = createRole("ADMIN");
        roleAdmin.addPermission(pAll);
        entityManager.persist(roleAdmin);

        log.info("Khởi tạo Roles và Permissions thành công!");
    }

    private Permission createPermission(String name, String description) {
        Permission permission = Permission.builder().name(name).description(description).build();
        entityManager.persist(permission);
        return permission;
    }

    private Role createRole(String name) {
        return Role.builder().name(name).build();
    }
}