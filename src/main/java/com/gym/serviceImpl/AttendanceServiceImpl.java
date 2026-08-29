package com.gym.serviceImpl;

import com.gym.dto.AttendanceDTO;
import com.gym.entity.Attendance;
import com.gym.entity.User;
import com.gym.exception.BadRequestException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.AttendanceRepository;
import com.gym.repository.UserRepository;
import com.gym.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AttendanceDTO checkIn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByUserIdAndDate(userId, today);
        if (existing.isPresent()) {
            throw new BadRequestException("User has already checked in today.");
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus("PRESENT");

        Attendance saved = attendanceRepository.save(attendance);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public AttendanceDTO checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today. Please check-in first."));

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("User has already checked out today.");
        }

        attendance.setCheckOutTime(LocalTime.now());
        Attendance saved = attendanceRepository.save(attendance);
        return convertToDTO(saved);
    }

    @Override
    public AttendanceDTO getTodayStatus(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, today).orElse(null);
        return attendance != null ? convertToDTO(attendance) : null;
    }

    @Override
    public List<AttendanceDTO> getMemberAttendanceHistory(Long userId) {
        List<Attendance> list = attendanceRepository.findByUserId(userId);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAllAttendanceForDate(LocalDate date) {
        List<Attendance> list = attendanceRepository.findByDate(date);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getAttendanceBetween(LocalDate start, LocalDate end) {
        List<Attendance> list = attendanceRepository.findByDateBetween(start, end);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDTO> getMemberAttendanceBetween(Long userId, LocalDate start, LocalDate end) {
        List<Attendance> list = attendanceRepository.findByUserIdAndDateBetween(userId, start, end);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setDate(attendance.getDate());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setStatus(attendance.getStatus());

        User user = attendance.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setFullName(user.getFullName());
            dto.setRole(user.getRoles().isEmpty() ? "" : user.getRoles().iterator().next().getName());
        }

        return dto;
    }
}
