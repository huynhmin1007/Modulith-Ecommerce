package com.dev.minn.ecommerce.identity.dto.request;

import com.dev.minn.ecommerce.identity.validation.annotation.ValidEmail;
import com.dev.minn.ecommerce.identity.validation.annotation.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TokenExchangeRequest {

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
