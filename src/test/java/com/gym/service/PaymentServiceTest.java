package com.gym.service;

import com.gym.dto.PaymentDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.*;
import com.gym.serviceImpl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock private PaymentRepository paymentRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private MembershipPlanRepository planRepository;

    private Member mockMember;
    private MembershipPlan mockPlan;
    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Jane Doe");
        user.setEmail("jane@test.com");
        user.setStatus("ACTIVE");

        mockPlan = new MembershipPlan();
        mockPlan.setId(2L);
        mockPlan.setName("Silver Plan");   // MembershipPlan uses 'name', not 'planName'
        mockPlan.setPrice(BigDecimal.valueOf(49.99));
        mockPlan.setDurationMonths(3);
        mockPlan.setStatus("ACTIVE");

        mockMember = new Member();
        mockMember.setId(5L);
        mockMember.setUser(user);
        mockMember.setMembershipPlan(mockPlan);
        mockMember.setJoinDate(LocalDate.now());
        mockMember.setMembershipExpiryDate(LocalDate.now().plusMonths(3));
        mockMember.setStatus("ACTIVE");

        mockPayment = new Payment();
        mockPayment.setId(100L);
        mockPayment.setMember(mockMember);
        mockPayment.setMembershipPlan(mockPlan);
        mockPayment.setAmount(BigDecimal.valueOf(49.99));
        mockPayment.setStatus("COMPLETED");
        mockPayment.setPaymentMethod("CREDIT_CARD");
        mockPayment.setPaymentDate(LocalDateTime.now());
        mockPayment.setTransactionId("TXN-TEST-001");
    }

    @Test
    void getPaymentsByMember_shouldReturnPayments_whenMemberExists() {
        when(paymentRepository.findByMemberId(5L)).thenReturn(List.of(mockPayment));

        List<PaymentDTO> result = paymentService.getPaymentsByMember(5L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("TXN-TEST-001");
        assertThat(result.get(0).getAmount()).isEqualTo(49.99);
    }

    @Test
    void getAllPayments_shouldReturnAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(mockPayment));

        List<PaymentDTO> result = paymentService.getAllPayments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void processPayment_shouldSavePayment_whenValidInput() {
        when(memberRepository.findById(5L)).thenReturn(Optional.of(mockMember));
        when(planRepository.findById(2L)).thenReturn(Optional.of(mockPlan));
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

        PaymentDTO input = new PaymentDTO();
        input.setMemberId(5L);
        input.setMembershipPlanId(2L);
        input.setAmount(49.99);
        input.setPaymentMethod("CREDIT_CARD");
        input.setStatus("COMPLETED");

        PaymentDTO result = paymentService.processPayment(input);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionId()).isEqualTo("TXN-TEST-001");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void processPayment_shouldThrow_whenMemberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        PaymentDTO input = new PaymentDTO();
        input.setMemberId(999L);
        input.setMembershipPlanId(2L);
        input.setAmount(49.99);
        input.setPaymentMethod("CASH");

        assertThatThrownBy(() -> paymentService.processPayment(input))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPaymentById_shouldReturnPayment_whenIdExists() {
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(mockPayment));

        PaymentDTO result = paymentService.getPaymentById(100L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getMemberName()).isEqualTo("Jane Doe");
    }
}
