package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "diet_plans")
public class DietPlan {

    @Id
    private Long id;

    private String planName;

    @DBRef
    private Member member;

    @DBRef
    private Trainer trainer;

    private Integer caloriesPerDay;

    private Integer proteinGrams;

    private Integer carbsGrams;

    private Integer fatsGrams;

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<DietMeal> meals = new ArrayList<>();

    public DietPlan() {}

    public DietPlan(String planName, Member member, Trainer trainer, Integer caloriesPerDay, Integer proteinGrams, Integer carbsGrams, Integer fatsGrams, String notes) {
        this.planName = planName;
        this.member = member;
        this.trainer = trainer;
        this.caloriesPerDay = caloriesPerDay;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatsGrams = fatsGrams;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Integer getCaloriesPerDay() { return caloriesPerDay; }
    public void setCaloriesPerDay(Integer caloriesPerDay) { this.caloriesPerDay = caloriesPerDay; }

    public Integer getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(Integer proteinGrams) { this.proteinGrams = proteinGrams; }

    public Integer getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(Integer carbsGrams) { this.carbsGrams = carbsGrams; }

    public Integer getFatsGrams() { return fatsGrams; }
    public void setFatsGrams(Integer fatsGrams) { this.fatsGrams = fatsGrams; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<DietMeal> getMeals() { return meals; }
    public void setMeals(List<DietMeal> meals) { this.meals = meals; }

    public void addMeal(DietMeal meal) {
        meals.add(meal);
        meal.setDietPlan(this);
    }
}
