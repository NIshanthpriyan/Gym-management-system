package com.gym.dto;

import java.time.LocalDateTime;

public class PaymentDTO {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long membershipPlanId;
    private String membershipPlanName;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private String pdfReceiptPath;

    public PaymentDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public Long getMembershipPlanId() {
        return membershipPlanId;
    }

    public void setMembershipPlanId(Long membershipPlanId) {
        this.membershipPlanId = membershipPlanId;
    }

    public String getMembershipPlanName() {
        return membershipPlanName;
    }

    public void setMembershipPlanName(String membershipPlanName) {
        this.membershipPlanName = membershipPlanName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPdfReceiptPath() {
        return pdfReceiptPath;
    }

    public void setPdfReceiptPath(String pdfReceiptPath) {
        this.pdfReceiptPath = pdfReceiptPath;
    }
}
