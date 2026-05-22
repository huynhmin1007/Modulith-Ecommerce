package com.dev.minn.ecommerce.identity.service;

import com.dev.minn.ecommerce.identity.dto.request.RegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.request.VerifyRegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.response.RegistrationInitiateResponse;
import com.dev.minn.ecommerce.identity.dto.response.UserInfo;

public interface UserManagementPort {
    RegistrationInitiateResponse initiateRegistration(RegistrationRequest request);
    UserInfo verifyRegistration(VerifyRegistrationRequest request);
    UserInfo getInfo(String userId);
}
