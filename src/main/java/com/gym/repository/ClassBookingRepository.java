package com.gym.repository;

import com.gym.entity.ClassBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {
    List<ClassBooking> findByMemberId(Long memberId);
    List<ClassBooking> findByGymClassId(Long gymClassId);
    Optional<ClassBooking> findByGymClassIdAndMemberId(Long gymClassId, Long memberId);
    boolean existsByGymClassIdAndMemberIdAndStatus(Long gymClassId, Long memberId, String status);
}
