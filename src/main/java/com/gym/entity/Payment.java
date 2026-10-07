package com.gym.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "payments")
public class Payment {

    @Id
    private Long id;

    @DBRef
    private Member member;

    @DBRef
    private MembershipPlan membershipPlan;

    private BigDecimal amount;

    private LocalDateTime paymentDate = LocalDateTime.now();

    private String paymentMethod = "CASH"; // CASH, CREDIT_CARD, DEBIT_CARD, UPI, BANK_TRANSFER

    @Indexed(unique = true, sparse = true)
    private String transactionId;

    private String status = "COMPLETED"; // COMPLETED, PENDING, FAILED

    private String pdfReceiptPath;

    public Payment() {}

    public Payment(Long id, Member member, MembershipPlan membershipPlan, BigDecimal amount, LocalDateTime paymentDate, String paymentMethod, String transactionId, String status, String pdfReceiptPath) {
        this.id = id;
        this.member = member;
        this.membershipPlan = membershipPlan;
        this.amount = amount;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.pdfReceiptPath = pdfReceiptPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(MembershipPlan membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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
