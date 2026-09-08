package com.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequestDTO {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message is too long")
    private String message;

    private String sessionId;

    public ChatRequestDTO() {
    }

    public ChatRequestDTO(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
