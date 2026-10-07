package com.gym.repository;

import com.gym.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, Long> {

    @Query("{ 'user.$id': ?0, 'date': ?1 }")
    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);

    @Query("{ 'user.$id': ?0 }")
    List<Attendance> findByUserId(Long userId);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{ 'user.$id': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
