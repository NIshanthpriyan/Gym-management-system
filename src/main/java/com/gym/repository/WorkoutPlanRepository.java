package com.gym.repository;

import com.gym.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByMemberId(Long memberId);
    Optional<WorkoutPlan> findFirstByMemberIdOrderByCreatedAtDesc(Long memberId);
}
