package com.gym.controller;

import com.gym.dto.TrainerDTO;
import com.gym.dto.MemberDTO;
import com.gym.service.TrainerService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/trainers")
    public ResponseEntity<Page<TrainerDTO>> getAllTrainers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        Page<TrainerDTO> trainers = trainerService.getAllTrainers(search, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/trainers/active")
    public ResponseEntity<List<TrainerDTO>> getAllActiveTrainers() {
        List<TrainerDTO> trainers = trainerService.getAllActiveTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/trainers/{id}")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable Long id) {
        TrainerDTO trainer = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }

    @GetMapping("/trainer/profile/{userId}")
    public ResponseEntity<TrainerDTO> getTrainerByUserId(@PathVariable Long userId) {
        TrainerDTO trainer = trainerService.getTrainerByUserId(userId);
        return ResponseEntity.ok(trainer);
    }

    @PostMapping("/trainers")
    public ResponseEntity<TrainerDTO> createTrainer(@RequestBody TrainerDTO trainerDTO) {
        TrainerDTO created = trainerService.createTrainer(trainerDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/trainers/{id}")
    public ResponseEntity<TrainerDTO> updateTrainer(@PathVariable Long id, @RequestBody TrainerDTO trainerDTO) {
        TrainerDTO updated = trainerService.updateTrainer(id, trainerDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/trainers/{id}")
    public ResponseEntity<String> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.ok("Trainer deleted successfully.");
    }

    // Get assigned members for a trainer
    @GetMapping("/trainer/{trainerId}/members")
    public ResponseEntity<List<MemberDTO>> getAssignedMembers(@PathVariable Long trainerId) {
        List<MemberDTO> members = memberService.getMembersByTrainerId(trainerId);
        return ResponseEntity.ok(members);
    }
}
