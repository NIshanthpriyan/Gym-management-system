package com.gym.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.dto.LoginRequest;
import com.gym.dto.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the AuthController.
 * Tests the full Spring context including security filter chain.
 *
 * NOTE: Requires a running test database (H2 in-memory or MySQL).
 * Configure src/test/resources/application-test.properties for H2.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static final String REGISTER_URL = "/api/auth/register/member";
    private static final String LOGIN_URL = "/api/auth/login";

    @Test
    @Order(1)
    void register_shouldReturn201_withValidMemberData() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("inttest_member");
        req.setEmail("inttest@member.com");
        req.setPassword("TestPass@123");
        req.setFullName("Integration Test User");
        req.setPhone("9876543210");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Member registered successfully"));
    }

    @Test
    @Order(2)
    void login_shouldReturn200AndToken_withValidCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    @Order(3)
    void login_shouldReturn401_withInvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrongpassword");

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void register_shouldReturn400_whenUsernameAlreadyTaken() throws Exception {
        // Try registering with same username from Order(1) test
        RegisterRequest req = new RegisterRequest();
        req.setUsername("inttest_member");
        req.setEmail("different@email.com");
        req.setPassword("Pass@123");
        req.setFullName("Another User");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("anotheruser");
        req.setEmail("not-an-email");  // Invalid email
        req.setPassword("Pass@123");
        req.setFullName("Another User");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }
}
