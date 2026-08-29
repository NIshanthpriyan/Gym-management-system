package com.gym.repository;

import com.gym.entity.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GymClassRepository extends JpaRepository<GymClass, Long> {
    List<GymClass> findByScheduleTimeAfterOrderByScheduleTimeAsc(LocalDateTime time);
    List<GymClass> findByTrainerId(Long trainerId);
}
