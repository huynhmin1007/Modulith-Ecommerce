package com.dev.minn.ecommerce.identity.event;

public record UserRegistrationInitiatedEvent(
        String email,
        String username,
        String otp,
        int timeCacheOtp
) {
}
