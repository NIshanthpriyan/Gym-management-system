package com.gym.repository;

import com.gym.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, Long>, MemberRepositoryCustom {

    @Query("{ 'user.$id': ?0 }")
    Optional<Member> findByUserId(Long userId);
    
    Page<Member> findByStatus(String status, Pageable pageable);
    
    @Query("{ 'membershipExpiryDate': { $lte: ?0 }, 'status': 'ACTIVE' }")
    List<Member> findExpiringMemberships(LocalDate date);
    
    @Query("{ 'trainer.$id': ?0 }")
    List<Member> findByTrainerId(Long trainerId);
}
