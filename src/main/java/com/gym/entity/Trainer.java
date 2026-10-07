package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "trainers")
public class Trainer {

    @Id
    private Long id;

    @DBRef
    private User user;

    private String specialization;

    private Integer experienceYears = 0;

    private BigDecimal salary = BigDecimal.ZERO;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    public Trainer() {}

    public Trainer(Long id, User user, String specialization, Integer experienceYears, BigDecimal salary, String status) {
        this.id = id;
        this.user = user;
        this.specialization = specialization;
        this.experienceYears = experienceYears;
        this.salary = salary;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", specialization='" + specialization + '\'' +
                ", experienceYears=" + experienceYears +
                ", salary=" + salary +
                ", status='" + status + '\'' +
                '}';
    }
}
