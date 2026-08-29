package com.gym.repository;

import com.gym.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByStatus(String status);
}
