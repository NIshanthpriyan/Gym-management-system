package com.gym.service;

import com.gym.dto.TrainerDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.*;
import com.gym.serviceImpl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock private TrainerRepository trainerRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private Trainer mockTrainer;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(2L);
        mockUser.setUsername("jane_trainer");
        mockUser.setFullName("Jane Trainer");
        mockUser.setEmail("jane@test.com");
        mockUser.setPhone("0987654321");
        mockUser.setStatus("ACTIVE");

        mockTrainer = new Trainer();
        mockTrainer.setId(1L);
        mockTrainer.setUser(mockUser);
        mockTrainer.setSpecialization("Yoga & Meditation");
        mockTrainer.setExperienceYears(6);
        mockTrainer.setSalary(BigDecimal.valueOf(3000.00));
        mockTrainer.setStatus("ACTIVE");
    }

    @Test
    void getAllTrainers_shouldReturnPagedResult() {
        Page<Trainer> mockPage = new PageImpl<>(List.of(mockTrainer));
        when(trainerRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<TrainerDTO> result = trainerService.getAllTrainers(null, null, 0, 10, "id", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Jane Trainer");
    }

    @Test
    void getTrainerById_shouldReturnTrainer_whenIdExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(mockTrainer));

        TrainerDTO result = trainerService.getTrainerById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("jane_trainer");
    }

    @Test
    void getTrainerById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.getTrainerById(999L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteTrainer_shouldCallDelete_whenTrainerExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(mockTrainer));

        trainerService.deleteTrainer(1L);

        verify(trainerRepository, times(1)).delete(mockTrainer);
    }
}
