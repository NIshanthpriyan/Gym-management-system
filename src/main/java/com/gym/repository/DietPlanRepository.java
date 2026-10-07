package com.gym.repository;

import com.gym.entity.DietPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietPlanRepository extends MongoRepository<DietPlan, Long> {

    @Query("{ 'member.$id': ?0 }")
    List<DietPlan> findByMemberId(Long memberId);

    @Query(value = "{ 'member.$id': ?0 }", sort = "{ 'createdAt': -1 }")
    Optional<DietPlan> findFirstByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("{ 'trainer.$id': ?0 }")
    List<DietPlan> findByTrainerId(Long trainerId);
}
