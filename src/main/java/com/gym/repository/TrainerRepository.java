package com.gym.repository;

import com.gym.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserId(Long userId);
    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE " +
           "LOWER(t.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.specialization) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Trainer> searchTrainers(@Param("query") String query, Pageable pageable);

    Page<Trainer> findByStatus(String status, Pageable pageable);

    List<Trainer> findByStatus(String status);
}
