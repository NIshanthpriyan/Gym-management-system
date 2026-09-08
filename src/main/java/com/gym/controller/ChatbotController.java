package com.gym.controller;

import com.gym.dto.ChatHistoryDTO;
import com.gym.dto.ChatRequestDTO;
import com.gym.dto.ChatResponseDTO;
import com.gym.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Send a message to the AI Gym Assistant and receive a contextual, safety-checked response.
     */
    @PostMapping("/chat/send")
    public ResponseEntity<ChatResponseDTO> sendMessage(
            @Valid @RequestBody ChatRequestDTO request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        ChatResponseDTO response = chatbotService.processChatMessage(username, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get chat history for the currently logged-in user.
     */
    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatHistoryDTO>> getMyChatHistory(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        List<ChatHistoryDTO> history = chatbotService.getUserChatHistory(username);
        return ResponseEntity.ok(history);
    }

    /**
     * Clear chat history for the currently logged-in user.
     */
    @DeleteMapping("/chat/clear")
    public ResponseEntity<Map<String, String>> clearMyChatHistory(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "anonymous";
        chatbotService.clearUserChatHistory(username);
        return ResponseEntity.ok(Map.of("message", "Chat history cleared successfully."));
    }

    /**
     * Admin endpoint: View all member chat logs for monitoring, auditing, and analytics.
     */
    @GetMapping("/admin/chat/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatHistoryDTO>> getAllChatLogs() {
        List<ChatHistoryDTO> logs = chatbotService.getAllChatLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * Admin endpoint: View chatbot statistics and active user metrics.
     */
    @GetMapping("/admin/chat/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getChatbotStats() {
        Map<String, Object> stats = chatbotService.getChatbotStatistics();
        return ResponseEntity.ok(stats);
    }
}
