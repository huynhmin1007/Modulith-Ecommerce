package com.dev.minn.ecommerce.identity.dto.request;

import com.dev.minn.ecommerce.identity.validation.annotation.ValidEmail;
import com.dev.minn.ecommerce.identity.validation.annotation.ValidPassword;
import lombok.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Getter
public class TokenExchangeRequest {

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
