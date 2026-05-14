package com.dev.minn.ecommerce.identity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TokenExchangeRequest {
    private String code;
}
