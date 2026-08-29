package com.gym.service;

import com.gym.dto.TrainerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TrainerService {
    Page<TrainerDTO> getAllTrainers(String query, String status, int page, int size, String sortBy, String sortDir);
    List<TrainerDTO> getAllActiveTrainers();
    TrainerDTO getTrainerById(Long id);
    TrainerDTO getTrainerByUserId(Long userId);
    TrainerDTO createTrainer(TrainerDTO trainerDTO);
    TrainerDTO updateTrainer(Long id, TrainerDTO trainerDTO);
    void deleteTrainer(Long id);
}
