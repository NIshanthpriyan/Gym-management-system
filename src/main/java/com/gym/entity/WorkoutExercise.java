package com.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workout_exercises")
public class WorkoutExercise {

    @Id
    private Long id;

    @DBRef
    @JsonIgnore
    private WorkoutPlan workoutPlan;

    private String dayOfWeek; // Monday, Tuesday, etc.

    private String exerciseName;

    private Integer sets = 3;

    private Integer reps = 10;

    private Integer weightLbs = 0;

    private String notes;

    public WorkoutExercise() {}

    public WorkoutExercise(Long id, WorkoutPlan workoutPlan, String dayOfWeek, String exerciseName, Integer sets, Integer reps, Integer weightLbs, String notes) {
        this.id = id;
        this.workoutPlan = workoutPlan;
        this.dayOfWeek = dayOfWeek;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weightLbs = weightLbs;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getWeightLbs() {
        return weightLbs;
    }

    public void setWeightLbs(Integer weightLbs) {
        this.weightLbs = weightLbs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
