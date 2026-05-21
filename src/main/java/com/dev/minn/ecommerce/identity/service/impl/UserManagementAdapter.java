package com.dev.minn.ecommerce.identity.service.impl;

import com.dev.minn.ecommerce.common.exception.BusinessException;
import com.dev.minn.ecommerce.common.exception.GlobalErrorCode;
import com.dev.minn.ecommerce.common.utils.OtpUtils;
import com.dev.minn.ecommerce.identity.constant.UserStatus;
import com.dev.minn.ecommerce.identity.dto.PendingRegistration;
import com.dev.minn.ecommerce.identity.dto.request.RegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.request.VerifyRegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.response.RegistrationInitiateResponse;
import com.dev.minn.ecommerce.identity.dto.response.UserInfo;
import com.dev.minn.ecommerce.identity.entity.Role;
import com.dev.minn.ecommerce.identity.entity.User;
import com.dev.minn.ecommerce.identity.event.UserRegisteredEvent;
import com.dev.minn.ecommerce.identity.event.UserRegistrationInitiatedEvent;
import com.dev.minn.ecommerce.identity.exception.IdentityErrorCode;
import com.dev.minn.ecommerce.identity.mapper.UserMapper;
import com.dev.minn.ecommerce.identity.repository.RoleRepository;
import com.dev.minn.ecommerce.identity.repository.UserRepository;
import com.dev.minn.ecommerce.identity.service.UserManagementPort;
import com.dev.minn.ecommerce.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserManagementAdapter implements UserManagementPort {

    ApplicationEventPublisher eventPublisher;

    RedisService redisService;

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    private final String KEY_PENDING_REGISTRATION = "user:pending-registration:";

    @Override
    public RegistrationInitiateResponse initiateRegistration(RegistrationRequest request) {
        String email = request.getEmail();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(IdentityErrorCode.USER_EXISTED);
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        String otp = OtpUtils.generateSecureOtp();

        PendingRegistration cacheInfo = userMapper.toPendingRegistration(request, otp);
        int expiresIn = 5 * 60;

        redisService.set(KEY_PENDING_REGISTRATION + email, cacheInfo, expiresIn, TimeUnit.SECONDS);

        eventPublisher.publishEvent(new UserRegistrationInitiatedEvent(
                email,
                cacheInfo.getUsername(),
                otp,
                expiresIn
        ));

        return RegistrationInitiateResponse.builder()
                .message("registration initiated successfully. Please verify your email.")
                .expiresIn(expiresIn)
                .build();
    }

    @Transactional
    @Override
    public UserInfo verifyRegistration(VerifyRegistrationRequest request) {
        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(IdentityErrorCode.USER_EXISTED);
        }

        PendingRegistration pendingInfo = redisService.get(KEY_PENDING_REGISTRATION + email, PendingRegistration.class);

        if (pendingInfo == null)
            throw new BusinessException(IdentityErrorCode.OTP_EXPIRED);

        if (!pendingInfo.getOtp().equals(request.getOtp()))
            throw new BusinessException(IdentityErrorCode.INVALID_OTP);

        redisService.delete(KEY_PENDING_REGISTRATION + email);

        Role role = roleRepository.findByName("CUSTOMER")
                .orElse(null);

        if (role == null) {
            log.error("Failed to create user: role not found");
            throw new BusinessException(GlobalErrorCode.INTERNAL_ERROR);
        }

        User user = userMapper.toUser(pendingInfo);
        user.setStatus(UserStatus.ACTIVE);
        user.addRole(role);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                user.getEmail(),
                user.getUsername()
        ));

        return userMapper.toUserInfo(userRepository.save(user));
    }

    @Override
    public UserInfo getInfo(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(IdentityErrorCode.USER_NOT_FOUND::throwException);
        return userMapper.toUserInfo(user);
    }
}
