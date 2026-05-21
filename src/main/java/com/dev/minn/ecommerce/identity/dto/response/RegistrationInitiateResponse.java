package com.dev.minn.ecommerce.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationInitiateResponse {
    String message;
    long expiresIn;
}
