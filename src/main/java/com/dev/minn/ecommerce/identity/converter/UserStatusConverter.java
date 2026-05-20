package com.dev.minn.ecommerce.identity.converter;

import com.dev.minn.ecommerce.identity.constant.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus.getStatus();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer value) {
        UserStatus status = UserStatus.fromStatus(value);
        if (status == null)
            throw new IllegalArgumentException("Invalid status value: " + value);

        return status;
    }
}

