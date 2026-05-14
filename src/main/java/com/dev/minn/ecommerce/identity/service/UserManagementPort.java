package com.dev.minn.ecommerce.identity.service;

public interface UserManagementPort {
    Object registerUser(Object request);
    Object getUserById(String id);
    void updatePassword(Object request);
}
