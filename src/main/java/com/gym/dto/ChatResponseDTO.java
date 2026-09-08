package com.gym.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatResponseDTO {

    private String reply;
    private String intentCategory;
    private List<String> suggestions = new ArrayList<>();
    private LocalDateTime timestamp;
    private Map<String, Object> accountSnippet;
    private boolean isMedicalSafetyNotice;

    public ChatResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatResponseDTO(String reply, String intentCategory, List<String> suggestions) {
        this.reply = reply;
        this.intentCategory = intentCategory;
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getIntentCategory() {
        return intentCategory;
    }

    public void setIntentCategory(String intentCategory) {
        this.intentCategory = intentCategory;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getAccountSnippet() {
        return accountSnippet;
    }

    public void setAccountSnippet(Map<String, Object> accountSnippet) {
        this.accountSnippet = accountSnippet;
    }

    public boolean isMedicalSafetyNotice() {
        return isMedicalSafetyNotice;
    }

    public boolean getIsMedicalSafetyNotice() {
        return isMedicalSafetyNotice;
    }

    public void setMedicalSafetyNotice(boolean medicalSafetyNotice) {
        this.isMedicalSafetyNotice = medicalSafetyNotice;
    }
}
