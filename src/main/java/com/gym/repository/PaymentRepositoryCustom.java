package com.gym.repository;

import java.time.LocalDateTime;

public interface PaymentRepositoryCustom {
    Double getTotalRevenue();
    Double getRevenueBetween(LocalDateTime start, LocalDateTime end);
}
