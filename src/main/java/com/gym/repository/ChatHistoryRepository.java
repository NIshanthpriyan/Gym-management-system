package com.gym.repository;

import com.gym.entity.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatHistory, Long>, ChatHistoryRepositoryCustom {

    @Query(value = "{ 'user.$id': ?0 }", sort = "{ 'timestamp': 1 }")
    List<ChatHistory> findByUserIdOrderByTimestampAsc(Long userId);

    List<ChatHistory> findBySessionIdOrderByTimestampAsc(String sessionId);

    List<ChatHistory> findAllByOrderByTimestampDesc();

    @Query(value = "{ 'user.$id': ?0 }", count = true)
    long countByUserId(Long userId);
}
