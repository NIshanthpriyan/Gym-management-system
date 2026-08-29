package com.gym.repository;

import com.gym.entity.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByMemberId(Long memberId);
    Optional<DietPlan> findFirstByMemberIdOrderByCreatedAtDesc(Long memberId);
    List<DietPlan> findByTrainerId(Long trainerId);
}
