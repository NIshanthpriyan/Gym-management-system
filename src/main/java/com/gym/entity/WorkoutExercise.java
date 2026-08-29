package com.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    @JsonIgnore
    private WorkoutPlan workoutPlan;

    @Column(name = "day_of_week", nullable = false, length = 15)
    private String dayOfWeek; // Monday, Tuesday, etc.

    @Column(name = "exercise_name", nullable = false, length = 100)
    private String exerciseName;

    @Column(nullable = false)
    private Integer sets = 3;

    @Column(nullable = false)
    private Integer reps = 10;

    @Column(name = "weight_lbs")
    private Integer weightLbs = 0;

    @Column(columnDefinition = "TEXT")
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
