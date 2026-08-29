package com.gym.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    private Long totalMembers;
    private Long activeMembers;
    private Long totalTrainers;

    private Long totalPlans;
    private Double totalRevenue;
    private Map<String, Double> monthlyRevenue; // Key: YYYY-MM, Value: Revenue Amount
    private Long expiringSoonCount;
    private List<PaymentDTO> recentPayments;
    private List<AttendanceDTO> recentAttendance;

    public DashboardStatsDTO() {}

    public Long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(Long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public Long getTotalTrainers() {
        return totalTrainers;
    }

    public void setTotalTrainers(Long totalTrainers) {
        this.totalTrainers = totalTrainers;
    }

    public Long getTotalPlans() {
        return totalPlans;
    }

    public void setTotalPlans(Long totalPlans) {
        this.totalPlans = totalPlans;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Double> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Map<String, Double> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Long getExpiringSoonCount() {
        return expiringSoonCount;
    }

    public void setExpiringSoonCount(Long expiringSoonCount) {
        this.expiringSoonCount = expiringSoonCount;
    }

    public List<PaymentDTO> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<PaymentDTO> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<AttendanceDTO> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<AttendanceDTO> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }
}
