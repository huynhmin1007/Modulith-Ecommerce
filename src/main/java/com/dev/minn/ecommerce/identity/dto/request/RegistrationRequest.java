package com.dev.minn.ecommerce.identity.dto.request;

import com.dev.minn.ecommerce.identity.validation.annotation.ValidEmail;
import com.dev.minn.ecommerce.identity.validation.annotation.ValidPassword;
import com.dev.minn.ecommerce.identity.validation.annotation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

    @ValidUsername
    String username;

    @NotBlank(message = "email is required")
    @ValidEmail
    String email;

    @ValidPassword
    String password;
}
