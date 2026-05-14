package com.dev.minn.ecommerce.identity.service;

import com.dev.minn.ecommerce.identity.dto.request.LogoutRequest;
import com.dev.minn.ecommerce.identity.dto.request.RefreshTokenRequest;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;

public interface AuthenticationPort {

    AuthenticationResponse exchangeToken(TokenExchangeRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
