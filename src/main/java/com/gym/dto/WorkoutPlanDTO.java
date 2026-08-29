package com.gym.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkoutPlanDTO {
    private Long id;
    private Long memberId;
    private String memberName;

    private String planName;
    private String description;
    private Integer durationWeeks;
    private LocalDateTime createdAt;
    private List<WorkoutExerciseDTO> exercises;

    public WorkoutPlanDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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

    public List<WorkoutExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseDTO> exercises) {
        this.exercises = exercises;
    }
}
