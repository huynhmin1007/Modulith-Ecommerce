package com.dev.minn.ecommerce.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest {

    @NotBlank(message = "refresh token is required")
    String refreshToken;
}
