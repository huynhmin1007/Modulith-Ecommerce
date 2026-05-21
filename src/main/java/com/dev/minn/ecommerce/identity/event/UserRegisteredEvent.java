package com.dev.minn.ecommerce.identity.event;

public record UserRegisteredEvent(
        String email,
        String username
) {
}
