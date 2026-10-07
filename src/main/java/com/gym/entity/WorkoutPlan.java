package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "workout_plans")
public class WorkoutPlan {

    @Id
    private Long id;

    @DBRef
    private Member member;

    private String planName;

    private String description;

    private Integer durationWeeks = 4;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<WorkoutExercise> exercises = new ArrayList<>();

    public WorkoutPlan() {}

    public WorkoutPlan(Long id, Member member, String planName, String description, Integer durationWeeks, LocalDateTime createdAt, List<WorkoutExercise> exercises) {
        this.id = id;
        this.member = member;
        this.planName = planName;
        this.description = description;
        this.durationWeeks = durationWeeks;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}
