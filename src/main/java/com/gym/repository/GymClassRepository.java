package com.gym.repository;

import com.gym.entity.GymClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GymClassRepository extends MongoRepository<GymClass, Long> {

    List<GymClass> findByScheduleTimeAfterOrderByScheduleTimeAsc(LocalDateTime time);

    @Query("{ 'trainer.$id': ?0 }")
    List<GymClass> findByTrainerId(Long trainerId);
}
