package com.gym.controller;

import com.gym.dto.WorkoutPlanDTO;
import com.gym.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkoutPlanController {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @PostMapping("/workout-plans")
    public ResponseEntity<WorkoutPlanDTO> createWorkoutPlan(@RequestBody WorkoutPlanDTO dto) {
        WorkoutPlanDTO created = workoutPlanService.createWorkoutPlan(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/member/{memberId}/workout-plan")
    public ResponseEntity<WorkoutPlanDTO> getLatestWorkoutPlan(@PathVariable Long memberId) {
        WorkoutPlanDTO plan = workoutPlanService.getLatestMemberWorkoutPlan(memberId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/member/{memberId}/workout-plans")
    public ResponseEntity<List<WorkoutPlanDTO>> getMemberWorkoutPlans(@PathVariable Long memberId) {
        List<WorkoutPlanDTO> plans = workoutPlanService.getMemberWorkoutPlans(memberId);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/workout-plans/{id}")
    public ResponseEntity<WorkoutPlanDTO> updateWorkoutPlan(@PathVariable Long id, @RequestBody WorkoutPlanDTO dto) {
        WorkoutPlanDTO updated = workoutPlanService.updateWorkoutPlan(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/workout-plans/{id}")
    public ResponseEntity<String> deleteWorkoutPlan(@PathVariable Long id) {
        workoutPlanService.deleteWorkoutPlan(id);
        return ResponseEntity.ok("Workout plan deleted successfully.");
    }
}
