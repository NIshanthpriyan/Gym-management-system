package com.gym.repository;

import com.gym.entity.ChatHistory;
import java.util.List;

public interface ChatHistoryRepositoryCustom {
    List<String> findDistinctUsernames();
    List<ChatHistory> findByUserUsernameOrderByTimestampAsc(String username);
    List<ChatHistory> findTop50ByUserUsernameOrderByTimestampDesc(String username);
    void deleteByUserUsername(String username);
}
