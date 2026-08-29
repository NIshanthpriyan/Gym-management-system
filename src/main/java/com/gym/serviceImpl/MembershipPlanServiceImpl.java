package com.gym.serviceImpl;

import com.gym.dto.PlanDTO;
import com.gym.entity.MembershipPlan;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MembershipPlanRepository;
import com.gym.service.MembershipPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    @Autowired
    private MembershipPlanRepository planRepository;

    @Override
    public List<PlanDTO> getAllPlans(String status) {
        List<MembershipPlan> plans;
        if (status != null && !status.trim().isEmpty()) {
            plans = planRepository.findByStatus(status);
        } else {
            plans = planRepository.findAll();
        }
        return plans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlanDTO getPlanById(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", id));
        return convertToDTO(plan);
    }

    @Override
    @Transactional
    public PlanDTO createPlan(PlanDTO planDTO) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(planDTO.getName());
        plan.setDurationMonths(planDTO.getDurationMonths());
        plan.setPrice(BigDecimal.valueOf(planDTO.getPrice()));
        plan.setDescription(planDTO.getDescription());
        plan.setStatus("ACTIVE");

        MembershipPlan savedPlan = planRepository.save(plan);
        return convertToDTO(savedPlan);
    }

    @Override
    @Transactional
    public PlanDTO updatePlan(Long id, PlanDTO planDTO) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", id));

        plan.setName(planDTO.getName());
        plan.setDurationMonths(planDTO.getDurationMonths());
        plan.setPrice(BigDecimal.valueOf(planDTO.getPrice()));
        plan.setDescription(planDTO.getDescription());
        if (planDTO.getStatus() != null) {
            plan.setStatus(planDTO.getStatus());
        }

        MembershipPlan updatedPlan = planRepository.save(plan);
        return convertToDTO(updatedPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", id));
        planRepository.delete(plan);
    }

    private PlanDTO convertToDTO(MembershipPlan plan) {
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDurationMonths(plan.getDurationMonths());
        dto.setPrice(plan.getPrice().doubleValue());
        dto.setDescription(plan.getDescription());
        dto.setStatus(plan.getStatus());
        return dto;
    }
}
