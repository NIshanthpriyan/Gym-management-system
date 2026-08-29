package com.gym.controller;

import com.gym.dto.ProgressDTO;
import com.gym.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/member/progress")
    public ResponseEntity<ProgressDTO> addProgressRecord(@RequestBody ProgressDTO progressDTO) {
        ProgressDTO created = progressService.addProgressRecord(progressDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/member/{memberId}/progress")
    public ResponseEntity<List<ProgressDTO>> getProgressHistory(@PathVariable Long memberId) {
        List<ProgressDTO> history = progressService.getMemberProgressHistory(memberId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/member/progress/{id}")
    public ResponseEntity<String> deleteProgressRecord(@PathVariable Long id) {
        progressService.deleteProgressRecord(id);
        return ResponseEntity.ok("Progress record deleted successfully.");
    }
}
