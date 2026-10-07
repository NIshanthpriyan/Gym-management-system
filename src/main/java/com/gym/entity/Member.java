package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "members")
public class Member {

    @Id
    private Long id;

    @DBRef
    private User user;

    @DBRef
    private MembershipPlan membershipPlan;

    @DBRef
    private Trainer trainer;

    private LocalDate joinDate;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE, EXPIRED

    private LocalDate membershipExpiryDate;

    private String emergencyContact;

    private String emergencyPhone;

    public Member() {}

    public Member(Long id, User user, MembershipPlan membershipPlan, LocalDate joinDate, String status, LocalDate membershipExpiryDate, String emergencyContact, String emergencyPhone) {
        this.id = id;
        this.user = user;
        this.membershipPlan = membershipPlan;
        this.joinDate = joinDate;
        this.status = status;
        this.membershipExpiryDate = membershipExpiryDate;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
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

    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(MembershipPlan membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getMembershipExpiryDate() {
        return membershipExpiryDate;
    }

    public void setMembershipExpiryDate(LocalDate membershipExpiryDate) {
        this.membershipExpiryDate = membershipExpiryDate;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", joinDate=" + joinDate +
                ", status='" + status + '\'' +
                ", membershipExpiryDate=" + membershipExpiryDate +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", emergencyPhone='" + emergencyPhone + '\'' +
                '}';
    }
}
