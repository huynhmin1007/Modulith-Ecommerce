package com.dev.minn.ecommerce.identity.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PendingRegistration {

    String email;
    String username;
    String password;
    String otp;
}
