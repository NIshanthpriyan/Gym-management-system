package com.gym.repository;

import com.gym.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TrainerRepositoryCustom {
    Optional<Trainer> findByUserUsername(String username);
    Page<Trainer> searchTrainers(String query, Pageable pageable);
}
