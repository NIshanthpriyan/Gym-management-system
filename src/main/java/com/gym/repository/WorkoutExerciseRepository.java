package com.gym.repository;

import com.gym.entity.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    List<WorkoutExercise> findByWorkoutPlanId(Long workoutPlanId);
}
