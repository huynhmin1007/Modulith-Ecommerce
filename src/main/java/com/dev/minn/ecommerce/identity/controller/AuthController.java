package com.dev.minn.ecommerce.identity.controller;

import com.dev.minn.ecommerce.identity.dto.request.LogoutRequest;
import com.dev.minn.ecommerce.identity.dto.request.RefreshTokenRequest;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;
import com.dev.minn.ecommerce.identity.service.impl.keycloak.KeycloakAuthenticationAdapter;
import com.dev.minn.ecommerce.identity.service.impl.keycloak.KeycloakProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    KeycloakAuthenticationAdapter keycloakAuthenticationAdapter;
    KeycloakProperties keycloakProperties;

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String authUrl = keycloakProperties.getAuthUrl();

        response.sendRedirect(authUrl);
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeToken(@RequestBody TokenExchangeRequest request) {
        log.info("Token exchange request: {}", request.getCode());

        AuthenticationResponse tokenResponse = keycloakAuthenticationAdapter.exchangeToken(request);

        ResponseCookie accessCookie = saveTokenCookie(tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), "access_token");
        ResponseCookie refreshCookie = saveTokenCookie(tokenResponse.getRefreshToken(), tokenResponse.getRefreshExpiresIn(), "refresh_token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Token exchange successful!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request: {}", request.getRefreshToken());

        AuthenticationResponse tokenResponse = keycloakAuthenticationAdapter.refreshToken(request);

        ResponseCookie accessCookie = saveTokenCookie(tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(), "access_token");
        ResponseCookie refreshCookie = saveTokenCookie(tokenResponse.getRefreshToken(), tokenResponse.getRefreshExpiresIn(), "refresh_token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Token exchange successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if(refreshToken != null && !refreshToken.isEmpty()) {
            keycloakAuthenticationAdapter.logout(new LogoutRequest(refreshToken));
        }

        ResponseCookie deleteAccessCookie = deleteTokenCookie("access_token");
        ResponseCookie deleteRefreshCookie = deleteTokenCookie("refresh_token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .body("Logged out successfully!");
    }

    private ResponseCookie saveTokenCookie(String token, long expiresIn, String cookieName) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(expiresIn)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie deleteTokenCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
