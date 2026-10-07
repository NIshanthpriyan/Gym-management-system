package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "class_bookings")
public class ClassBooking {

    @Id
    private Long id;

    @DBRef
    private GymClass gymClass;

    @DBRef
    private Member member;

    private LocalDateTime bookingTime = LocalDateTime.now();

    private String status = "CONFIRMED"; // CONFIRMED, CANCELLED

    public ClassBooking() {}

    public ClassBooking(GymClass gymClass, Member member) {
        this.gymClass = gymClass;
        this.member = member;
        this.bookingTime = LocalDateTime.now();
        this.status = "CONFIRMED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GymClass getGymClass() { return gymClass; }
    public void setGymClass(GymClass gymClass) { this.gymClass = gymClass; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
