package com.gym.controller;

import com.gym.entity.DietMeal;
import com.gym.entity.DietPlan;
import com.gym.entity.Member;
import com.gym.entity.Trainer;
import com.gym.repository.DietPlanRepository;
import com.gym.repository.MemberRepository;
import com.gym.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DietPlanController {

    @Autowired
    private DietPlanRepository dietPlanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @GetMapping("/member/{memberId}/diet-plan")
    public ResponseEntity<?> getLatestDietPlan(@PathVariable Long memberId) {
        return dietPlanRepository.findFirstByMemberIdOrderByCreatedAtDesc(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/diet-plans")
    public ResponseEntity<List<DietPlan>> getAllDietPlans() {
        return ResponseEntity.ok(dietPlanRepository.findAll());
    }

    @PostMapping("/diet-plans")
    public ResponseEntity<?> createDietPlan(@RequestBody Map<String, Object> payload) {
        try {
            Long memberId = Long.valueOf(payload.get("memberId").toString());
            String planName = (String) payload.get("planName");
            Integer caloriesPerDay = Integer.valueOf(payload.get("caloriesPerDay").toString());
            Integer proteinGrams = Integer.valueOf(payload.get("proteinGrams").toString());
            Integer carbsGrams = Integer.valueOf(payload.get("carbsGrams").toString());
            Integer fatsGrams = Integer.valueOf(payload.get("fatsGrams").toString());
            String notes = (String) payload.get("notes");

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            Trainer trainer = null;
            if (payload.get("trainerId") != null) {
                Long trainerId = Long.valueOf(payload.get("trainerId").toString());
                trainer = trainerRepository.findById(trainerId).orElse(null);
            }

            DietPlan plan = new DietPlan(planName, member, trainer, caloriesPerDay, proteinGrams, carbsGrams, fatsGrams, notes);

            if (payload.get("meals") != null) {
                List<Map<String, Object>> mealsList = (List<Map<String, Object>>) payload.get("meals");
                for (Map<String, Object> m : mealsList) {
                    String mealType = (String) m.get("mealType");
                    String foodItems = (String) m.get("foodItems");
                    Integer calories = Integer.valueOf(m.get("calories").toString());
                    DietMeal meal = new DietMeal(mealType, foodItems, calories);
                    plan.addMeal(meal);
                }
            }

            DietPlan saved = dietPlanRepository.save(plan);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating diet plan: " + e.getMessage());
        }
    }

    @DeleteMapping("/diet-plans/{id}")
    public ResponseEntity<String> deleteDietPlan(@PathVariable Long id) {
        if (!dietPlanRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dietPlanRepository.deleteById(id);
        return ResponseEntity.ok("Diet plan deleted successfully.");
    }
}
