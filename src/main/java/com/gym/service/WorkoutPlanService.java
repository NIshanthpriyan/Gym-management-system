package com.gym.service;

import com.gym.dto.WorkoutPlanDTO;
import java.util.List;

public interface WorkoutPlanService {
    WorkoutPlanDTO createWorkoutPlan(WorkoutPlanDTO dto);
    WorkoutPlanDTO getWorkoutPlanById(Long id);
    WorkoutPlanDTO getLatestMemberWorkoutPlan(Long memberId);
    List<WorkoutPlanDTO> getMemberWorkoutPlans(Long memberId);
    WorkoutPlanDTO updateWorkoutPlan(Long id, WorkoutPlanDTO dto);
    void deleteWorkoutPlan(Long id);
}
