package com.gym.controller;

import com.gym.dto.AttendanceDTO;
import com.gym.dto.DashboardStatsDTO;
import com.gym.dto.PaymentDTO;
import com.gym.entity.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalMembers(memberRepository.count());
        stats.setTotalTrainers(trainerRepository.count());
        
        // Count active members
        long activeCount = memberRepository.findAll().stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()))
                .count();
        stats.setActiveMembers(activeCount);

        stats.setTotalPlans(planRepository.count());

        Double totalRevenueVal = paymentRepository.getTotalRevenue();
        stats.setTotalRevenue(totalRevenueVal != null ? totalRevenueVal : 0.0);

        // Compute monthly revenue map for current year
        Map<String, Double> monthlyRev = new LinkedHashMap<>();
        int currentYear = LocalDate.now().getYear();
        List<Payment> completedPayments = paymentRepository.findAll().stream()
                .filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%d-%02d", currentYear, month);
            monthlyRev.put(monthKey, 0.0);
        }

        for (Payment p : completedPayments) {
            LocalDateTime pDate = p.getPaymentDate();
            if (pDate.getYear() == currentYear) {
                String monthKey = pDate.format(monthYearFormatter);
                monthlyRev.put(monthKey, monthlyRev.getOrDefault(monthKey, 0.0) + p.getAmount().doubleValue());
            }
        }
        stats.setMonthlyRevenue(monthlyRev);

        // Count members expiring in the next 7 days
        LocalDate threshold = LocalDate.now().plusDays(7);
        long expiringSoon = memberRepository.findAll().stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()) 
                        && m.getMembershipExpiryDate() != null 
                        && !m.getMembershipExpiryDate().isBefore(LocalDate.now())
                        && !m.getMembershipExpiryDate().isAfter(threshold))
                .count();
        stats.setExpiringSoonCount(expiringSoon);

        // Get 5 recent payments
        Pageable recentPaymentsPageable = PageRequest.of(0, 5, Sort.by("paymentDate").descending());
        List<PaymentDTO> recentPayments = paymentRepository.findAll(recentPaymentsPageable).getContent().stream()
                .map(p -> {
                    PaymentDTO d = new PaymentDTO();
                    d.setId(p.getId());
                    d.setMemberName(p.getMember().getUser().getFullName());
                    d.setAmount(p.getAmount().doubleValue());
                    d.setPaymentDate(p.getPaymentDate());
                    d.setPaymentMethod(p.getPaymentMethod());
                    d.setStatus(p.getStatus());
                    d.setTransactionId(p.getTransactionId());
                    return d;
                }).collect(Collectors.toList());
        stats.setRecentPayments(recentPayments);

        // Get 5 recent attendances
        Pageable recentAttendancePageable = PageRequest.of(0, 5, Sort.by("date").descending());
        List<AttendanceDTO> recentAttendance = attendanceRepository.findAll(recentAttendancePageable).getContent().stream()
                .map(a -> {
                    AttendanceDTO d = new AttendanceDTO();
                    d.setId(a.getId());
                    d.setFullName(a.getUser().getFullName());
                    d.setDate(a.getDate());
                    d.setCheckInTime(a.getCheckInTime());
                    d.setCheckOutTime(a.getCheckOutTime());
                    d.setStatus(a.getStatus());
                    return d;
                }).collect(Collectors.toList());
        stats.setRecentAttendance(recentAttendance);

        return ResponseEntity.ok(stats);
    }
}
