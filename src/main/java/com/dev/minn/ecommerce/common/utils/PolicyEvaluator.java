package com.dev.minn.ecommerce.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component("iam")
public class PolicyEvaluator {

    /**
     * =========================================================
     * CHECK PERMISSIONS
     * =========================================================
     */
    public boolean has(String... requiredPermissions) {

        Set<String> authorities = extractAuthorities();

        if (authorities.isEmpty()) {
            return false;
        }

        return Arrays.stream(requiredPermissions)
                .anyMatch(required ->
                        authorities.stream()
                                .anyMatch(granted ->
                                        matches(granted, required)
                                )
                );
    }

    /**
     * =========================================================
     * WILDCARD MATCH
     * =========================================================
     *
     * granted = permission user có
     * required = permission API yêu cầu
     */
    private boolean matches(String granted, String required) {

        String[] grantedParts = granted.split(":");
        String[] requiredParts = required.split(":");

        if (grantedParts.length != 4 || requiredParts.length != 4) {
            return false;
        }

        for (int i = 0; i < 4; i++) {

            // wildcard
            if ("*".equals(grantedParts[i])) {
                continue;
            }

            // mismatch
            if (!grantedParts[i].equals(requiredParts[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * =========================================================
     * EXTRACT AUTHORITIES
     * =========================================================
     */
    private Set<String> extractAuthorities() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Set.of();
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}