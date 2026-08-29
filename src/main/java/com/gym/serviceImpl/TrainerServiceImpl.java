package com.gym.serviceImpl;

import com.gym.dto.TrainerDTO;
import com.gym.entity.*;
import com.gym.exception.BadRequestException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.*;
import com.gym.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<TrainerDTO> getAllTrainers(String query, String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Trainer> trainersPage;
        if (query != null && !query.trim().isEmpty()) {
            trainersPage = trainerRepository.searchTrainers(query, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            trainersPage = trainerRepository.findByStatus(status, pageable);
        } else {
            trainersPage = trainerRepository.findAll(pageable);
        }

        List<TrainerDTO> dtoList = trainersPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, trainersPage.getTotalElements());
    }

    @Override
    public List<TrainerDTO> getAllActiveTrainers() {
        return trainerRepository.findByStatus("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainerDTO getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));
        return convertToDTO(trainer);
    }

    @Override
    public TrainerDTO getTrainerByUserId(Long userId) {
        Trainer trainer = trainerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "userId", userId));
        return convertToDTO(trainer);
    }

    @Override
    @Transactional
    public TrainerDTO createTrainer(TrainerDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "trainer123"));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());
        user.setDob(dto.getDob());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        Role trainerRole = roleRepository.findByName("ROLE_TRAINER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: ROLE_TRAINER"));
        user.setRoles(Collections.singleton(trainerRole));

        User savedUser = userRepository.save(user);

        // Create Trainer details
        Trainer trainer = new Trainer();
        trainer.setUser(savedUser);
        trainer.setSpecialization(dto.getSpecialization());
        trainer.setExperienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0);
        trainer.setSalary(dto.getSalary() != null ? BigDecimal.valueOf(dto.getSalary()) : BigDecimal.ZERO);
        trainer.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        Trainer savedTrainer = trainerRepository.save(trainer);
        return convertToDTO(savedTrainer);
    }

    @Override
    @Transactional
    public TrainerDTO updateTrainer(Long id, TrainerDTO dto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));

        User user = trainer.getUser();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());
        user.setDob(dto.getDob());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
            trainer.setStatus(dto.getStatus());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(user);

        trainer.setSpecialization(dto.getSpecialization());
        trainer.setExperienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0);
        trainer.setSalary(dto.getSalary() != null ? BigDecimal.valueOf(dto.getSalary()) : BigDecimal.ZERO);

        Trainer updatedTrainer = trainerRepository.save(trainer);
        return convertToDTO(updatedTrainer);
    }

    @Override
    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", id));
        trainerRepository.delete(trainer); // Will cascade delete user
    }

    private TrainerDTO convertToDTO(Trainer trainer) {
        TrainerDTO dto = new TrainerDTO();
        dto.setId(trainer.getId());
        dto.setSpecialization(trainer.getSpecialization());
        dto.setExperienceYears(trainer.getExperienceYears());
        dto.setSalary(trainer.getSalary() != null ? trainer.getSalary().doubleValue() : 0.0);
        dto.setStatus(trainer.getStatus());

        User user = trainer.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setFullName(user.getFullName());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
            dto.setGender(user.getGender());
            dto.setDob(user.getDob());
            dto.setProfilePicture(user.getProfilePicture());
        }

        return dto;
    }
}
