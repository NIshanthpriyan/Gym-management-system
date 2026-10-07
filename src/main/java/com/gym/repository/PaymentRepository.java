package com.gym.repository;

import com.gym.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, Long>, PaymentRepositoryCustom {

    @Query("{ 'member.$id': ?0 }")
    List<Payment> findByMemberId(Long memberId);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
}
