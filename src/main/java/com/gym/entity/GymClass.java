package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "gym_classes")
public class GymClass {

    @Id
    private Long id;

    private String className;

    @DBRef
    private Trainer trainer;

    private LocalDateTime scheduleTime;

    private String room = "Studio A";

    private Integer capacity = 20;

    private Integer bookedCount = 0;

    private String description;

    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED

    public GymClass() {}

    public GymClass(String className, Trainer trainer, LocalDateTime scheduleTime, String room, Integer capacity, String description) {
        this.className = className;
        this.trainer = trainer;
        this.scheduleTime = scheduleTime;
        this.room = room;
        this.capacity = capacity;
        this.description = description;
        this.bookedCount = 0;
        this.status = "SCHEDULED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public LocalDateTime getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(LocalDateTime scheduleTime) { this.scheduleTime = scheduleTime; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
