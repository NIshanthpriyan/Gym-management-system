package com.gym.service;

import com.gym.dto.AttendanceDTO;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceDTO checkIn(Long userId);
    AttendanceDTO checkOut(Long userId);
    AttendanceDTO getTodayStatus(Long userId);
    List<AttendanceDTO> getMemberAttendanceHistory(Long userId);
    List<AttendanceDTO> getAllAttendanceForDate(LocalDate date);
    List<AttendanceDTO> getAttendanceBetween(LocalDate start, LocalDate end);
    List<AttendanceDTO> getMemberAttendanceBetween(Long userId, LocalDate start, LocalDate end);
}
