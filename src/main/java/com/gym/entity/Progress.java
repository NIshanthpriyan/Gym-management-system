package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "progress")
public class Progress {

    @Id
    private Long id;

    @DBRef
    private Member member;

    private LocalDate recordedDate;

    private BigDecimal weight; // kg

    private BigDecimal height; // meters

    private BigDecimal bodyFatPercentage;

    private BigDecimal muscleMass;

    private BigDecimal chest;

    private BigDecimal waist;

    private BigDecimal hips;

    private BigDecimal bmi;

    private String notes;

    public Progress() {}

    public Progress(Long id, Member member, LocalDate recordedDate, BigDecimal weight, BigDecimal height, BigDecimal bodyFatPercentage, BigDecimal muscleMass, BigDecimal chest, BigDecimal waist, BigDecimal hips, BigDecimal bmi, String notes) {
        this.id = id;
        this.member = member;
        this.recordedDate = recordedDate;
        this.weight = weight;
        this.height = height;
        this.bodyFatPercentage = bodyFatPercentage;
        this.muscleMass = muscleMass;
        this.chest = chest;
        this.waist = waist;
        this.hips = hips;
        this.bmi = bmi;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(LocalDate recordedDate) {
        this.recordedDate = recordedDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(BigDecimal bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public BigDecimal getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(BigDecimal muscleMass) {
        this.muscleMass = muscleMass;
    }

    public BigDecimal getChest() {
        return chest;
    }

    public void setChest(BigDecimal chest) {
        this.chest = chest;
    }

    public BigDecimal getWaist() {
        return waist;
    }

    public void setWaist(BigDecimal waist) {
        this.waist = waist;
    }

    public BigDecimal getHips() {
        return hips;
    }

    public void setHips(BigDecimal hips) {
        this.hips = hips;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
