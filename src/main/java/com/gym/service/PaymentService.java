package com.gym.service;

import com.gym.dto.PaymentDTO;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface PaymentService {
    PaymentDTO processPayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getAllPayments();
    List<PaymentDTO> getPaymentsByMember(Long memberId);
    ByteArrayInputStream generateReceiptPdf(Long paymentId);
}
