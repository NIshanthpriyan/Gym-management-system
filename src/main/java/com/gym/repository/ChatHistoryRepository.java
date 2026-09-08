package com.gym.repository;

import com.gym.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUserIdOrderByTimestampAsc(Long userId);

    List<ChatHistory> findByUserUsernameOrderByTimestampAsc(String username);

    List<ChatHistory> findTop50ByUserUsernameOrderByTimestampDesc(String username);

    List<ChatHistory> findBySessionIdOrderByTimestampAsc(String sessionId);

    List<ChatHistory> findAllByOrderByTimestampDesc();

    long countByUserId(Long userId);

    @Query("SELECT DISTINCT c.user.username FROM ChatHistory c")
    List<String> findDistinctUsernames();

    void deleteByUserUsername(String username);
}
