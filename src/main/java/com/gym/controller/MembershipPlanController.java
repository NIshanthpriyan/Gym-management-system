package com.gym.controller;

import com.gym.dto.PlanDTO;
import com.gym.service.MembershipPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MembershipPlanController {

    @Autowired
    private MembershipPlanService planService;

    @GetMapping("/plans")
    public ResponseEntity<List<PlanDTO>> getActivePlans() {
        List<PlanDTO> plans = planService.getAllPlans("ACTIVE");
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/all")
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        List<PlanDTO> plans = planService.getAllPlans(null);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable Long id) {
        PlanDTO plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/admin/plans")
    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanDTO planDTO) {
        PlanDTO created = planService.createPlan(planDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/admin/plans/{id}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable Long id, @RequestBody PlanDTO planDTO) {
        PlanDTO updated = planService.updatePlan(id, planDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/plans/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok("Plan deleted successfully.");
    }
}
