package com.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "diet_meals")
public class DietMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id", nullable = false)
    @JsonIgnore
    private DietPlan dietPlan;

    @Column(name = "meal_type", nullable = false, length = 30) // BREAKFAST, LUNCH, SNACK, DINNER
    private String mealType;

    @Column(name = "food_items", columnDefinition = "TEXT", nullable = false)
    private String foodItems;

    @Column(name = "calories")
    private Integer calories;

    public DietMeal() {}

    public DietMeal(String mealType, String foodItems, Integer calories) {
        this.mealType = mealType;
        this.foodItems = foodItems;
        this.calories = calories;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DietPlan getDietPlan() { return dietPlan; }
    public void setDietPlan(DietPlan dietPlan) { this.dietPlan = dietPlan; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getFoodItems() { return foodItems; }
    public void setFoodItems(String foodItems) { this.foodItems = foodItems; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
}
