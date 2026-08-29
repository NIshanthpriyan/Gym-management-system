package com.gym.service;

import com.gym.dto.PlanDTO;
import java.util.List;

public interface MembershipPlanService {
    List<PlanDTO> getAllPlans(String status);
    PlanDTO getPlanById(Long id);
    PlanDTO createPlan(PlanDTO planDTO);
    PlanDTO updatePlan(Long id, PlanDTO planDTO);
    void deletePlan(Long id);
}
