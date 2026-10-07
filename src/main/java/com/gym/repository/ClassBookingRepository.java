package com.gym.repository;

import com.gym.entity.ClassBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends MongoRepository<ClassBooking, Long> {

    @Query("{ 'member.$id': ?0 }")
    List<ClassBooking> findByMemberId(Long memberId);

    @Query("{ 'gymClass.$id': ?0 }")
    List<ClassBooking> findByGymClassId(Long gymClassId);

    @Query("{ 'gymClass.$id': ?0, 'member.$id': ?1 }")
    Optional<ClassBooking> findByGymClassIdAndMemberId(Long gymClassId, Long memberId);

    @Query(value = "{ 'gymClass.$id': ?0, 'member.$id': ?1, 'status': ?2 }", exists = true)
    boolean existsByGymClassIdAndMemberIdAndStatus(Long gymClassId, Long memberId, String status);
}
