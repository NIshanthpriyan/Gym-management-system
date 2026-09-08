package com.gym.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_message", nullable = false, length = 2000)
    private String userMessage;

    @Column(name = "bot_response", nullable = false, length = 5000)
    private String botResponse;

    @Column(name = "intent_category", length = 50)
    private String intentCategory;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public ChatHistory() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatHistory(User user, String userMessage, String botResponse, String intentCategory, String sessionId) {
        this.user = user;
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.intentCategory = intentCategory;
        this.sessionId = sessionId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters

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
