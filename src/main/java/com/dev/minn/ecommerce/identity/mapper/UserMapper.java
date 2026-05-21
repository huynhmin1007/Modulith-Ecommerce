package com.dev.minn.ecommerce.identity.mapper;

import com.dev.minn.ecommerce.identity.dto.PendingRegistration;
import com.dev.minn.ecommerce.identity.dto.request.RegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.response.UserInfo;
import com.dev.minn.ecommerce.identity.entity.User;
import com.dev.minn.ecommerce.identity.entity.associate.UserRole;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default PendingRegistration toPendingRegistration(RegistrationRequest request, String otp) {
        return PendingRegistration.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .otp(otp)
                .build();
    }

    User toUser(PendingRegistration pendingInfo);

    UserInfo toUserInfo(User user);

    default Set<String> mapRoles(Set<UserRole> roles) {
        if (roles == null) return Collections.emptySet();

        return roles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());
    }
}
