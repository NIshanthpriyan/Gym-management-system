package com.gym.repository;

import com.gym.entity.MembershipPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MembershipPlanRepository extends MongoRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByStatus(String status);
}
