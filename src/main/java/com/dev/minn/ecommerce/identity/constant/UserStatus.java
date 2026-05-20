package com.dev.minn.ecommerce.identity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {

    INACTIVE(0), ACTIVE(1), BANNED(2), DELETED(3), PENDING(4);

    private final int status;

    public static UserStatus fromStatus(Integer status) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.getStatus() == status) {
                return userStatus;
            }
        }
        return null;
    }


    public static UserStatus fromStatus(String status) {
        if (status == null)
            return null;

        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.name().equalsIgnoreCase(status)) {
                return userStatus;
            }
        }
        return null;
    }
}
