package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_history")
public class ChatHistory {

    @Id
    private Long id;

    @DBRef
    private User user;

    @Indexed
    private String username;

    private String userMessage;

    private String botResponse;

    private String intentCategory;

    private String sessionId;

    private LocalDateTime timestamp;

    public ChatHistory() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatHistory(User user, String userMessage, String botResponse, String intentCategory, String sessionId) {
        this.user = user;
        this.username = user != null ? user.getUsername() : null;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.intentCategory = intentCategory;
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.username = user.getUsername();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(String botResponse) {
        this.botResponse = botResponse;
    }

    public String getIntentCategory() {
        return intentCategory;
    }

    public void setIntentCategory(String intentCategory) {
        this.intentCategory = intentCategory;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
