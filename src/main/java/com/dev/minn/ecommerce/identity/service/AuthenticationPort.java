package com.dev.minn.ecommerce.identity.service;

import com.dev.minn.ecommerce.identity.dto.TokenPayload;
import com.dev.minn.ecommerce.identity.dto.request.LogoutRequest;
import com.dev.minn.ecommerce.identity.dto.request.RefreshTokenRequest;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;

public interface AuthenticationPort {

    AuthenticationResponse exchangeToken(TokenExchangeRequest request);
    TokenPayload verifyAndExtract(String token, String expectedTokenType);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
    void revokeToken(String token, long timeoutMillis);
    Object extractPayload(String token);
}
