package com.gym.service;

import com.gym.dto.ChatHistoryDTO;
import com.gym.dto.ChatRequestDTO;
import com.gym.dto.ChatResponseDTO;

import java.util.List;
import java.util.Map;

public interface ChatbotService {

    ChatResponseDTO processChatMessage(String username, ChatRequestDTO request);

    List<ChatHistoryDTO> getUserChatHistory(String username);

    void clearUserChatHistory(String username);

    List<ChatHistoryDTO> getAllChatLogs();

    Map<String, Object> getChatbotStatistics();
}
