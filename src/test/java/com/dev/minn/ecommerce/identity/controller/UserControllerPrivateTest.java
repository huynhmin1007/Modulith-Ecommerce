package com.dev.minn.ecommerce.identity.controller;

import com.dev.minn.ecommerce.common.config.AppProperties;
import com.dev.minn.ecommerce.common.utils.PolicyEvaluator;
import com.dev.minn.ecommerce.identity.config.SecurityConfig;
import com.dev.minn.ecommerce.identity.dto.response.UserInfo;
import com.dev.minn.ecommerce.identity.service.AuthenticationPort;
import com.dev.minn.ecommerce.identity.service.UserManagementPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        AppProperties.class,
        SecurityConfig.class,
        PolicyEvaluator.class
})
public class UserControllerPrivateTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserManagementPort userManagementPort;

    @MockitoBean
    AuthenticationPort authenticationPort;

    @Test
    @DisplayName("Should get current user info")
    void shouldGetCurrentUserInfo_successfully_whenLoggedIn() throws Exception {
        UserInfo response = UserInfo.builder()
                .id("user-id")
                .username("test")
                .email("user@example.com")
                .build();

        when(userManagementPort.getInfo("user-id"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/users/me")
                                .with(jwt().jwt(jwt ->
                                        jwt.subject("user-id")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(response.getId()))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));

        verify(userManagementPort).getInfo("user-id");
    }

    @Test
    @DisplayName("Should not allow user to access other user profile")
    void shouldGetUserInfo_failed_whenDontHavePermission() throws Exception {
        mockMvc.perform(
                        get("/users/user-1")
                                .with(jwt().jwt(jwt -> jwt.subject("user-2"))
                                        .authorities(new SimpleGrantedAuthority("identity:user:read:own")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow admin access any profile")
    void shouldGetUserInfo_successfully_whenHavePermission() throws Exception {
        String userId = "user-id";

        UserInfo response = UserInfo.builder()
                .id(userId)
                .username("test")
                .email("test@example.com")
                .build();

        when(userManagementPort.getInfo(userId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/users/" + userId)
                                .with(jwt().jwt(jwt -> jwt.subject("admin-id"))
                                        .authorities(new SimpleGrantedAuthority("identity:user:read:any")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(response.getId()))
                .andExpect(jsonPath("$.data.username").value(response.getUsername()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));

        verify(userManagementPort).getInfo(userId);
    }
}
