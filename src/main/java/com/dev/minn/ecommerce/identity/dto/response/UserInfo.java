package com.dev.minn.ecommerce.identity.dto.response;

import com.dev.minn.ecommerce.identity.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserInfo {

    String id;
    String username;
    String email;
    String status;
    Set<String> roles;
}
