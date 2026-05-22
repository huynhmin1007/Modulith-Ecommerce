package com.dev.minn.ecommerce.identity.controller;

import com.dev.minn.ecommerce.common.utils.SecurityUtils;
import com.dev.minn.ecommerce.identity.dto.request.RegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.request.VerifyRegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.response.RegistrationInitiateResponse;
import com.dev.minn.ecommerce.identity.dto.response.UserInfo;
import com.dev.minn.ecommerce.identity.service.UserManagementPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserManagementPort userManagementPort;

    @PostMapping("/public/register")
    public RegistrationInitiateResponse register(
            @Valid @RequestBody RegistrationRequest request
    ) {
        return userManagementPort.initiateRegistration(request);
    }

    @PostMapping("/public/register/verify")
    public UserInfo verifyRegistration(@Valid @RequestBody VerifyRegistrationRequest request) {
        return userManagementPort.verifyRegistration(request);
    }

    @GetMapping("/me")
    public UserInfo getInfo() {
        return userManagementPort.getInfo(SecurityUtils.getCurrentUserId());
    }

    @PreAuthorize("@iam.has('identity:user:read:any') or authentication.name == #id")
    @GetMapping("/{id}")
    public UserInfo getInfo(@PathVariable String id, Authentication authentication) {
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities());
        return userManagementPort.getInfo(id);
    }
}
