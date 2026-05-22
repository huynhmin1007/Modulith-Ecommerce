package com.dev.minn.ecommerce.identity.controller;

import com.dev.minn.ecommerce.identity.dto.request.RegistrationRequest;
import com.dev.minn.ecommerce.identity.dto.response.RegistrationInitiateResponse;
import com.dev.minn.ecommerce.identity.service.AuthenticationPort;
import com.dev.minn.ecommerce.identity.service.UserManagementPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerPublicTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    UserManagementPort userManagementPort;

    @MockitoBean
    AuthenticationPort authenticationPort;

    @Test
    @DisplayName("Should initiate registration successfully")
    void shouldInitiateRegistration_successfully_whenRequestIsValid() throws Exception {
        RegistrationRequest request = RegistrationRequest.builder()
                .username("test")
                .email("test@example.com")
                .password("Password123@")
                .build();

        RegistrationInitiateResponse response = RegistrationInitiateResponse.builder()
                .message("success")
                .expiresIn(300)
                .build();

        when(userManagementPort.initiateRegistration(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/users/public/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.message")
                        .value("success"))
                .andExpect(jsonPath("$.data.expiresIn")
                        .value(300));

        verify(userManagementPort)
                .initiateRegistration(any(RegistrationRequest.class));
    }
}
