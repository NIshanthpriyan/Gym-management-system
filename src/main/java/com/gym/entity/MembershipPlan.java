package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "membership_plans")
public class MembershipPlan {

    @Id
    private Long id;

    @Indexed(unique = true)
    private String name;

    private Integer durationMonths;

    private BigDecimal price;

    private String description;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    public MembershipPlan() {}

    public MembershipPlan(Long id, String name, Integer durationMonths, BigDecimal price, String description, String status) {
        this.id = id;
        this.name = name;
        this.durationMonths = durationMonths;
        this.price = price;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
