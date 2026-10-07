package com.gym.repository;

import com.gym.entity.WorkoutExercise;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface WorkoutExerciseRepository extends MongoRepository<WorkoutExercise, Long> {

    @Query("{ 'workoutPlan.$id': ?0 }")
    List<WorkoutExercise> findByWorkoutPlanId(Long workoutPlanId);
}
