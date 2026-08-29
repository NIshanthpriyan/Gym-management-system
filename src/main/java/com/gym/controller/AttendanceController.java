package com.gym.controller;

import com.gym.dto.AttendanceDTO;
import com.gym.entity.User;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.UserRepository;
import com.gym.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/attendance/check-in")
    public ResponseEntity<AttendanceDTO> selfCheckIn(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AttendanceDTO dto = attendanceService.checkIn(user.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/attendance/check-out")
    public ResponseEntity<AttendanceDTO> selfCheckOut(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AttendanceDTO dto = attendanceService.checkOut(user.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/admin/attendance/check-in/{userId}")
    public ResponseEntity<AttendanceDTO> manualCheckIn(@PathVariable Long userId) {
        AttendanceDTO dto = attendanceService.checkIn(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/admin/attendance/check-out/{userId}")
    public ResponseEntity<AttendanceDTO> manualCheckOut(@PathVariable Long userId) {
        AttendanceDTO dto = attendanceService.checkOut(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/attendance/status")
    public ResponseEntity<AttendanceDTO> getTodayStatus(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AttendanceDTO dto = attendanceService.getTodayStatus(user.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/attendance/history")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendanceHistory(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<AttendanceDTO> list = attendanceService.getMemberAttendanceHistory(user.getId());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/admin/attendance/scan")
    public ResponseEntity<?> scanQrCodeCheckIn(@RequestBody java.util.Map<String, String> payload) {
        try {
            String code = payload.get("qrCode");
            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("QR Code is required");
            }
            code = code.trim();
            Long userId = null;
            if (code.startsWith("GYM-MEMBER-")) {
                final Long targetId = Long.parseLong(code.replace("GYM-MEMBER-", ""));
                User user = userRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found for member ID " + targetId));
                userId = user.getId();
            } else if (code.matches("\\d+")) {
                userId = Long.parseLong(code);
            } else {
                User user = userRepository.findByUsername(code)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + code));
                userId = user.getId();
            }
            AttendanceDTO dto = attendanceService.checkIn(userId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Scan check-in failed: " + e.getMessage());
        }
    }

    @GetMapping("/admin/attendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceLogs(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<AttendanceDTO> list;
        if (date != null) {
            list = attendanceService.getAllAttendanceForDate(date);
        } else if (startDate != null && endDate != null) {
            list = attendanceService.getAttendanceBetween(startDate, endDate);
        } else {
            list = attendanceService.getAllAttendanceForDate(LocalDate.now());
        }
        return ResponseEntity.ok(list);
    }
}
