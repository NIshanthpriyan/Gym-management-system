package com.gym.repository;

import com.gym.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);
    Optional<Member> findByUserUsername(String username);
    
    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.user.phone) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Member> searchMembers(@Param("query") String query, Pageable pageable);
    
    Page<Member> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.membershipExpiryDate <= :date AND m.status = 'ACTIVE'")
    List<Member> findExpiringMemberships(@Param("date") LocalDate date);
    
    List<Member> findByTrainerId(Long trainerId);
    List<Member> findByTrainerUserId(Long userId);
}
