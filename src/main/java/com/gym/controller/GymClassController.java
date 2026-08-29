package com.gym.controller;

import com.gym.entity.ClassBooking;
import com.gym.entity.GymClass;
import com.gym.entity.Member;
import com.gym.entity.Trainer;
import com.gym.repository.ClassBookingRepository;
import com.gym.repository.GymClassRepository;
import com.gym.repository.MemberRepository;
import com.gym.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GymClassController {

    @Autowired
    private GymClassRepository gymClassRepository;

    @Autowired
    private ClassBookingRepository classBookingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @GetMapping("/classes")
    public ResponseEntity<List<GymClass>> getAllClasses() {
        return ResponseEntity.ok(gymClassRepository.findAll());
    }

    @PostMapping("/classes")
    public ResponseEntity<?> createClass(@RequestBody Map<String, Object> payload) {
        try {
            String className = (String) payload.get("className");
            String room = payload.getOrDefault("room", "Studio A").toString();
            Integer capacity = Integer.valueOf(payload.get("capacity").toString());
            String description = (String) payload.get("description");
            String scheduleTimeStr = (String) payload.get("scheduleTime"); // ISO string e.g., "2026-07-25T10:00:00"

            LocalDateTime scheduleTime = LocalDateTime.parse(scheduleTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Trainer trainer = null;
            if (payload.get("trainerId") != null && !payload.get("trainerId").toString().isEmpty()) {
                Long trainerId = Long.valueOf(payload.get("trainerId").toString());
                trainer = trainerRepository.findById(trainerId).orElse(null);
            }

            GymClass gymClass = new GymClass(className, trainer, scheduleTime, room, capacity, description);
            GymClass saved = gymClassRepository.save(gymClass);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating class: " + e.getMessage());
        }
    }

    @PostMapping("/classes/{classId}/book")
    public ResponseEntity<?> bookClass(@PathVariable Long classId, @RequestParam Long memberId) {
        try {
            GymClass gymClass = gymClassRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            if (gymClass.getBookedCount() >= gymClass.getCapacity()) {
                return ResponseEntity.badRequest().body("Class is fully booked!");
            }

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            if (classBookingRepository.existsByGymClassIdAndMemberIdAndStatus(classId, memberId, "CONFIRMED")) {
                return ResponseEntity.badRequest().body("You have already booked this class!");
            }

            ClassBooking booking = new ClassBooking(gymClass, member);
            ClassBooking saved = classBookingRepository.save(booking);

            gymClass.setBookedCount(gymClass.getBookedCount() + 1);
            gymClassRepository.save(gymClass);

            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error booking class: " + e.getMessage());
        }
    }

    @GetMapping("/member/{memberId}/class-bookings")
    public ResponseEntity<List<ClassBooking>> getMemberBookings(@PathVariable Long memberId) {
        return ResponseEntity.ok(classBookingRepository.findByMemberId(memberId));
    }

    @DeleteMapping("/class-bookings/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        return classBookingRepository.findById(id).map(booking -> {
            booking.setStatus("CANCELLED");
            classBookingRepository.save(booking);

            GymClass gymClass = booking.getGymClass();
            if (gymClass != null && gymClass.getBookedCount() > 0) {
                gymClass.setBookedCount(gymClass.getBookedCount() - 1);
                gymClassRepository.save(gymClass);
            }
            return ResponseEntity.ok("Booking cancelled.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
