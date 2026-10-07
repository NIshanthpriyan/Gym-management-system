package com.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "diet_meals")
public class DietMeal {

    @Id
    private Long id;

    @DBRef
    @JsonIgnore
    private DietPlan dietPlan;

    private String mealType; // BREAKFAST, LUNCH, SNACK, DINNER

    private String foodItems;

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
