package com.gym.serviceImpl;

import com.gym.dto.WorkoutExerciseDTO;
import com.gym.dto.WorkoutPlanDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MemberRepository;

import com.gym.repository.WorkoutPlanRepository;
import com.gym.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private MemberRepository memberRepository;



    @Override
    @Transactional
    public WorkoutPlanDTO createWorkoutPlan(WorkoutPlanDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", dto.getMemberId()));

        WorkoutPlan plan = new WorkoutPlan();
        plan.setMember(member);
        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setDurationWeeks(dto.getDurationWeeks() != null ? dto.getDurationWeeks() : 4);

        if (dto.getExercises() != null) {
            long exId = 1L;
            for (WorkoutExerciseDTO exDTO : dto.getExercises()) {
                WorkoutExercise exercise = new WorkoutExercise();
                exercise.setId(exDTO.getId() != null ? exDTO.getId() : exId++);
                exercise.setWorkoutPlan(plan);
                exercise.setDayOfWeek(exDTO.getDayOfWeek());
                exercise.setExerciseName(exDTO.getExerciseName());
                exercise.setSets(exDTO.getSets() != null ? exDTO.getSets() : 3);
                exercise.setReps(exDTO.getReps() != null ? exDTO.getReps() : 10);
                exercise.setWeightLbs(exDTO.getWeightLbs() != null ? exDTO.getWeightLbs() : 0);
                exercise.setNotes(exDTO.getNotes());
                plan.getExercises().add(exercise);
            }
        }

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        return convertToDTO(saved);
    }

    @Override
    public WorkoutPlanDTO getWorkoutPlanById(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", id));
        return convertToDTO(plan);
    }

    @Override
    public WorkoutPlanDTO getLatestMemberWorkoutPlan(Long memberId) {
        WorkoutPlan plan = workoutPlanRepository.findFirstByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout Plan not found for member: " + memberId));
        return convertToDTO(plan);
    }

    @Override
    public List<WorkoutPlanDTO> getMemberWorkoutPlans(Long memberId) {
        return workoutPlanRepository.findByMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public WorkoutPlanDTO updateWorkoutPlan(Long id, WorkoutPlanDTO dto) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", id));

        plan.setPlanName(dto.getPlanName());
        plan.setDescription(dto.getDescription());
        plan.setDurationWeeks(dto.getDurationWeeks() != null ? dto.getDurationWeeks() : 4);

        // Clear and reload exercises (mappedBy orphanRemoval handles table cleanup)
        plan.getExercises().clear();
        if (dto.getExercises() != null) {
            long exId = 1L;
            for (WorkoutExerciseDTO exDTO : dto.getExercises()) {
                WorkoutExercise exercise = new WorkoutExercise();
                exercise.setId(exDTO.getId() != null ? exDTO.getId() : exId++);
                exercise.setWorkoutPlan(plan);
                exercise.setDayOfWeek(exDTO.getDayOfWeek());
                exercise.setExerciseName(exDTO.getExerciseName());
                exercise.setSets(exDTO.getSets() != null ? exDTO.getSets() : 3);
                exercise.setReps(exDTO.getReps() != null ? exDTO.getReps() : 10);
                exercise.setWeightLbs(exDTO.getWeightLbs() != null ? exDTO.getWeightLbs() : 0);
                exercise.setNotes(exDTO.getNotes());
                plan.getExercises().add(exercise);
            }
        }

        WorkoutPlan updated = workoutPlanRepository.save(plan);
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteWorkoutPlan(Long id) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", id));
        workoutPlanRepository.delete(plan);
    }

    private WorkoutPlanDTO convertToDTO(WorkoutPlan plan) {
        WorkoutPlanDTO dto = new WorkoutPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setDescription(plan.getDescription());
        dto.setDurationWeeks(plan.getDurationWeeks());
        dto.setCreatedAt(plan.getCreatedAt());

        if (plan.getMember() != null) {
            dto.setMemberId(plan.getMember().getId());
            if (plan.getMember().getUser() != null) {
                dto.setMemberName(plan.getMember().getUser().getFullName());
            }
        }



        List<WorkoutExerciseDTO> exercises = plan.getExercises().stream().map(ex -> {
            WorkoutExerciseDTO exDTO = new WorkoutExerciseDTO();
            exDTO.setId(ex.getId());
            exDTO.setDayOfWeek(ex.getDayOfWeek());
            exDTO.setExerciseName(ex.getExerciseName());
            exDTO.setSets(ex.getSets());
            exDTO.setReps(ex.getReps());
            exDTO.setWeightLbs(ex.getWeightLbs());
            exDTO.setNotes(ex.getNotes());
            return exDTO;
        }).collect(Collectors.toList());

        dto.setExercises(exercises);
        return dto;
    }
}
