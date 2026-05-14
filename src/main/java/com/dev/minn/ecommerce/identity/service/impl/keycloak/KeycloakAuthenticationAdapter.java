package com.dev.minn.ecommerce.identity.service.impl.keycloak;

import com.dev.minn.ecommerce.common.exception.BusinessException;
import com.dev.minn.ecommerce.identity.dto.request.LogoutRequest;
import com.dev.minn.ecommerce.identity.dto.request.RefreshTokenRequest;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;
import com.dev.minn.ecommerce.identity.exception.IdentityErrorCode;
import com.dev.minn.ecommerce.identity.service.AuthenticationPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeycloakAuthenticationAdapter implements AuthenticationPort {

    KeycloakProperties keycloakProperties;
    RestClient restClient = RestClient.create();

    @Override
    public AuthenticationResponse exchangeToken(TokenExchangeRequest request) {
        log.info("Token exchange request: {}", request.getCode());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", keycloakProperties.clientId);
        formData.add("client_secret", keycloakProperties.clientSecret);
        formData.add("code", request.getCode());
        formData.add("redirect_uri", keycloakProperties.redirectUri);
        formData.add("scope", "openid email profile");

        return restClient.post()
                .uri(keycloakProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new BusinessException(IdentityErrorCode.INVALID_AUTH_CODE);
                })
                .body(AuthenticationResponse.class);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", keycloakProperties.clientId);
        formData.add("client_secret", keycloakProperties.clientSecret);
        formData.add("refresh_token", request.getRefreshToken());

        return restClient.post()
                .uri(keycloakProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new BusinessException(IdentityErrorCode.INVALID_REFRESH_TOKEN);
                })
                .body(AuthenticationResponse.class);
    }

    @Override
    public void logout(LogoutRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", keycloakProperties.clientId);
        formData.add("client_secret", keycloakProperties.clientSecret);
        formData.add("refresh_token", request.getRefreshToken());

        restClient.post()
                .uri(keycloakProperties.getLogoutUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new BusinessException(IdentityErrorCode.LOGOUT_FAILED);
                })
                .body(Void.class);
    }
}
