package com.gym.repository;

import com.gym.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Double getTotalRevenue() {
        Query query = new Query(Criteria.where("status").is("COMPLETED"));
        List<Payment> payments = mongoTemplate.find(query, Payment.class);
        return payments.stream()
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue())
                .sum();
    }

    @Override
    public Double getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("status").is("COMPLETED")
                .and("paymentDate").gte(start).lte(end));
        List<Payment> payments = mongoTemplate.find(query, Payment.class);
        return payments.stream()
                .filter(p -> p.getAmount() != null)
                .mapToDouble(p -> p.getAmount().doubleValue())
                .sum();
    }
}
