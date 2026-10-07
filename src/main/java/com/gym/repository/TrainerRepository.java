package com.gym.repository;

import com.gym.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends MongoRepository<Trainer, Long>, TrainerRepositoryCustom {

    @Query("{ 'user.$id': ?0 }")
    Optional<Trainer> findByUserId(Long userId);

    Page<Trainer> findByStatus(String status, Pageable pageable);

    List<Trainer> findByStatus(String status);
}
