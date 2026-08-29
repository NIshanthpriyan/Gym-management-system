package com.gym.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private Long memberOrTrainerId; // Profile id if Member/Trainer, null if Admin

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String username, String email, List<String> roles, Long memberOrTrainerId) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.memberOrTrainerId = memberOrTrainerId;
    }

    public AuthResponse(String token, String tokenType, Long userId, String username, String email, List<String> roles, Long memberOrTrainerId) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.memberOrTrainerId = memberOrTrainerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getMemberOrTrainerId() {
        return memberOrTrainerId;
    }

    public void setMemberOrTrainerId(Long memberOrTrainerId) {
        this.memberOrTrainerId = memberOrTrainerId;
    }
}
