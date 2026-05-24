package com.dev.minn.ecommerce.identity.service;

import com.dev.minn.ecommerce.common.application.exception.BusinessException;
import com.dev.minn.ecommerce.identity.config.RsaKeyProperties;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;
import com.dev.minn.ecommerce.identity.entity.User;
import com.dev.minn.ecommerce.identity.exception.IdentityErrorCode;
import com.dev.minn.ecommerce.identity.repository.UserRepository;
import com.dev.minn.ecommerce.identity.service.impl.jwt.JwtAuthenticationAdapter;
import com.dev.minn.ecommerce.identity.service.impl.jwt.JwtProperties;
import com.dev.minn.ecommerce.identity.utils.IdentityTestFactory;
import com.dev.minn.ecommerce.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationAdapterTest {

    @Mock
    RedisService redisService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    JwtProperties jwtProperties;
    RsaKeyProperties rsaKeys;

    @InjectMocks
    JwtAuthenticationAdapter authenticationAdapter;

    @BeforeEach
    public void setUp() throws Exception {
        jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("test-issuer");
        jwtProperties.setAccessValidity(3600);
        jwtProperties.setRefreshValidity(86400);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair keyPair = generator.generateKeyPair();

        rsaKeys = new RsaKeyProperties(
                (RSAPublicKey) keyPair.getPublic(),
                (RSAPrivateKey) keyPair.getPrivate()
        );

        authenticationAdapter = new JwtAuthenticationAdapter(
                redisService,
                userRepository,
                rsaKeys,
                jwtProperties,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("Should exchange token successfully")
    void exchangeToken_success() {
        User user = IdentityTestFactory.customer(null);

        TokenExchangeRequest request = TokenExchangeRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);

        AuthenticationResponse response = authenticationAdapter.exchangeToken(request);

        assertNotNull(response);

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());

        assertEquals(
                jwtProperties.accessValidity,
                response.getExpiresIn()
        );

        assertEquals(
                jwtProperties.refreshValidity,
                response.getRefreshExpiresIn()
        );
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void exchangeToken_invalidPassword() {
        User user = IdentityTestFactory.customer(null);

        TokenExchangeRequest request = TokenExchangeRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authenticationAdapter.exchangeToken(request)
        );

        assertEquals(
                IdentityErrorCode.INVALID_CREDENTIALS,
                exception.getErrorCode()
        );
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void exchangeToken_userNotFound() {

        TokenExchangeRequest request =
                TokenExchangeRequest.builder()
                        .email("notfound@example.com")
                        .password("Password123@")
                        .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authenticationAdapter.exchangeToken(request)
        );

        assertEquals(
                IdentityErrorCode.USER_NOT_FOUND,
                exception.getErrorCode()
        );
    }
}
