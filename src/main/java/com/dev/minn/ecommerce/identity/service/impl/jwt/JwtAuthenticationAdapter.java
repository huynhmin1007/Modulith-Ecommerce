package com.dev.minn.ecommerce.identity.service.impl.jwt;

import com.dev.minn.ecommerce.common.exception.BusinessException;
import com.dev.minn.ecommerce.identity.config.RsaKeyProperties;
import com.dev.minn.ecommerce.identity.dto.TokenPayload;
import com.dev.minn.ecommerce.identity.dto.request.LogoutRequest;
import com.dev.minn.ecommerce.identity.dto.request.RefreshTokenRequest;
import com.dev.minn.ecommerce.identity.dto.request.TokenExchangeRequest;
import com.dev.minn.ecommerce.identity.dto.response.AuthenticationResponse;
import com.dev.minn.ecommerce.identity.entity.User;
import com.dev.minn.ecommerce.identity.exception.IdentityErrorCode;
import com.dev.minn.ecommerce.identity.repository.UserRepository;
import com.dev.minn.ecommerce.identity.service.AuthenticationPort;
import com.dev.minn.ecommerce.redis.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtAuthenticationAdapter implements AuthenticationPort {

    RedisService redisService;

    UserRepository userRepository;

    RsaKeyProperties rsaKeys;
    JwtProperties jwtProperties;
    PasswordEncoder passwordEncoder;

    final static String BLACKLIST_PREFIX = "blacklist:jti:";
    final static String BANNED_SESSION_PREFIX = "banned-session:";

    @Override
    public AuthenticationResponse exchangeToken(TokenExchangeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(IdentityErrorCode.USER_NOT_FOUND::throwException);

        boolean isMatchingPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isMatchingPassword) {
            throw new BusinessException(IdentityErrorCode.INVALID_CREDENTIALS);
        }

        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .expiresIn(jwtProperties.accessValidity)
                .refreshExpiresIn(jwtProperties.refreshValidity)
                .build();
    }

    @Override
    public TokenPayload verifyAndExtract(String token, String expectedTokenType) {
        try {
            JWTClaimsSet claims = extractClaims(token);
            String tokenType = claims.getStringClaim("tokenType");

            if (!expectedTokenType.equalsIgnoreCase(tokenType)) {
                throw new BusinessException(IdentityErrorCode.INVALID_TOKEN);
            }

            boolean isBlacklisted = redisService.exists(BLACKLIST_PREFIX + claims.getJWTID());
            if (isBlacklisted) {
                throw new BusinessException(IdentityErrorCode.TOKEN_REVOKED);
            }

            String accountId = claims.getSubject();
            Instant issueTime = claims.getIssueTime() != null ? claims.getIssueTime().toInstant() : null;
            String bannedEpochStr = redisService.get(BANNED_SESSION_PREFIX + accountId, String.class);

            if (bannedEpochStr != null && issueTime != null) {
                long bannedEpoch = Long.parseLong(bannedEpochStr);
                long tokenEpoch = issueTime.toEpochMilli();

                if (tokenEpoch <= bannedEpoch) {
                    throw new BusinessException(IdentityErrorCode.TOKEN_REVOKED);
                }
            }

            String scopeStr = claims.getStringClaim("scope");
            List<String> authorityList = Collections.emptyList();
            if (StringUtils.hasText(scopeStr)) {
                authorityList = Arrays.asList(scopeStr.split(" "));
            }

            return TokenPayload.builder()
                    .accountId(accountId)
                    .authorities(authorityList)
                    .email(claims.getStringClaim("email"))
                    .build();
        } catch (ParseException e) {
            log.error("Failed to verify JWT token: {}", token, e);
            throw new BusinessException(IdentityErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        JWTClaimsSet claims = extractClaims(request.getRefreshToken());

        String jti = claims.getJWTID();
        Date expirationTime = claims.getExpirationTime();

        long timeToLiveMillis = expirationTime.getTime() - System.currentTimeMillis();
        if (timeToLiveMillis > 0) {
            revokeToken(jti, timeToLiveMillis);
        }

        User user = userRepository.findById(UUID.fromString(claims.getSubject()))
                .orElseThrow(IdentityErrorCode.USER_NOT_FOUND::throwException);

        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .expiresIn(jwtProperties.accessValidity)
                .refreshExpiresIn(jwtProperties.refreshValidity)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
    }

    @Override
    public void revokeToken(String token, long timeoutMillis) {
        redisService.set(BLACKLIST_PREFIX + token, "revoked", timeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object extractPayload(String token) {
        return extractClaims(token);
    }

    private String generateAccessToken(User user) {
        try {
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            Instant now = Instant.now();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.issuer)
                    .subject(user.getId().toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(jwtProperties.accessValidity)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .claim("email", user.getEmail())
                    .claim("tokenType", "access")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new RSASSASigner(rsaKeys.privateKey()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to sign JWT for account: {}", user.getEmail(), e);
            throw new RuntimeException("Internal error while generating token");
        }
    }

    private String generateRefreshToken(User user) {
        try {
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            Instant now = Instant.now();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.issuer)
                    .subject(user.getId().toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(jwtProperties.refreshValidity)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("email", user.getEmail())
                    .claim("tokenType", "refresh")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new RSASSASigner(rsaKeys.privateKey()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to sign JWT for account: {}", user.getEmail(), e);
            throw new RuntimeException("Internal error while generating token");
        }
    }

    private String buildScope(User user) {
        Set<String> authorities = new HashSet<>();

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRolesAsRole().forEach(role -> {
                authorities.add("ROLE_" + role.getName().toUpperCase());

                if (!CollectionUtils.isEmpty(role.getPermissionsAsPermission())) {
                    role.getPermissionsAsPermission().forEach(permission ->
                            authorities.add(permission.getName().toUpperCase())
                    );
                }
            });
        }

        return String.join(" ", authorities);
    }

    private JWTClaimsSet extractClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RSASSAVerifier verifier = new RSASSAVerifier(rsaKeys.publicKey());
            if (!signedJWT.verify(verifier)) {
                throw new BusinessException(IdentityErrorCode.INVALID_TOKEN);
            }

            if (claims.getExpirationTime() == null || new Date().after(claims.getExpirationTime())) {
                throw new BusinessException(IdentityErrorCode.TOKEN_EXPIRED);
            }

            return claims;
        } catch (JOSEException | ParseException e) {
            log.error("Failed to parse or verify JWT token", e);
            throw new BusinessException(IdentityErrorCode.INVALID_TOKEN);
        }
    }
}
