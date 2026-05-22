package com.dev.minn.ecommerce.identity.dto.request;

import com.dev.minn.ecommerce.identity.validation.annotation.ValidPassword;
import com.dev.minn.ecommerce.identity.validation.annotation.ValidUsername;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AuthenticationRequest {

    @ValidUsername
    String username;

    @ValidPassword
    String password;
}
