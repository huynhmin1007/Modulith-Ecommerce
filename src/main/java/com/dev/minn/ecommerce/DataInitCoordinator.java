package com.dev.minn.ecommerce;

import com.dev.minn.ecommerce.identity.RolePermissionInit;
import com.dev.minn.ecommerce.identity.UserInit;
import com.dev.minn.ecommerce.notification.NotificationDataInitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DataInitCoordinator implements ApplicationRunner {

    RolePermissionInit rolePermissionInit;
    UserInit userInit;
    NotificationDataInitService notificationInit;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        log.info("=== BẮT ĐẦU QUÁ TRÌNH INIT DATA CHUNG ===");

        try {
            rolePermissionInit.initRolesAndPermissions();
            userInit.initUsers();
            notificationInit.init();

            log.info("=== QUÁ TRÌNH INIT DATA HOÀN TẤT THÀNH CÔNG ===");
        } catch (Exception e) {
            log.error("Lỗi trong quá trình khởi tạo dữ liệu mồi: ", e);
        }
    }
}
