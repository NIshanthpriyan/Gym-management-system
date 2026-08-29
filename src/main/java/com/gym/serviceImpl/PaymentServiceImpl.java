package com.gym.serviceImpl;

import com.gym.dto.PaymentDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.PaymentRepository;
import com.gym.service.PaymentService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        Member member = memberRepository.findById(paymentDTO.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", paymentDTO.getMemberId()));

        MembershipPlan plan = planRepository.findById(paymentDTO.getMembershipPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", paymentDTO.getMembershipPlanId()));

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setMembershipPlan(plan);
        payment.setAmount(BigDecimal.valueOf(paymentDTO.getAmount()));
        payment.setPaymentMethod(paymentDTO.getPaymentMethod() != null ? paymentDTO.getPaymentMethod() : "CASH");
        payment.setStatus("COMPLETED");

        // Generate Transaction ID if not provided
        if (paymentDTO.getTransactionId() == null || paymentDTO.getTransactionId().trim().isEmpty()) {
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            payment.setTransactionId(paymentDTO.getTransactionId());
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Update member active status and extension
        member.setStatus("ACTIVE");
        member.getUser().setStatus("ACTIVE");
        
        LocalDate currentExpiry = member.getMembershipExpiryDate();
        if (currentExpiry == null || currentExpiry.isBefore(LocalDate.now())) {
            member.setMembershipExpiryDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
        } else {
            member.setMembershipExpiryDate(currentExpiry.plusMonths(plan.getDurationMonths()));
        }
        member.setMembershipPlan(plan);
        memberRepository.save(member);

        return convertToDTO(savedPayment);
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return convertToDTO(payment);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ByteArrayInputStream generateReceiptPdf(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Styling / Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(33, 37, 41));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            // Document Header
            Paragraph title = new Paragraph("GYM FIT CENTRE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph address = new Paragraph("123 Fitness Avenue, Silicon Valley / Phone: +1 555-829-0123 / support@gymfit.com", subTitleFont);
            address.setAlignment(Element.ALIGN_CENTER);
            address.setSpacingAfter(20);
            document.add(address);

            // Line Separator
            Paragraph line = new Paragraph("----------------------------------------------------------------------------------------------------------------------------------", subTitleFont);
            line.setSpacingAfter(20);
            document.add(line);

            // Transaction Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            PdfPCell cell1 = new PdfPCell(new Phrase("Invoice details:", boldFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Customer details:", boldFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell2);

            // Invoice details values
            String paymentDateStr = payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            PdfPCell invDetails = new PdfPCell(new Phrase(
                    "Transaction ID: " + payment.getTransactionId() + "\n" +
                    "Payment Date: " + paymentDateStr + "\n" +
                    "Payment Method: " + payment.getPaymentMethod() + "\n" +
                    "Status: " + payment.getStatus(), regularFont));
            invDetails.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(invDetails);

            // Member details values
            Member member = payment.getMember();
            PdfPCell memberDetails = new PdfPCell(new Phrase(
                    "Name: " + member.getUser().getFullName() + "\n" +
                    "Email: " + member.getUser().getEmail() + "\n" +
                    "Phone: " + member.getUser().getPhone(), regularFont));
            memberDetails.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(memberDetails);

            document.add(infoTable);

            // Ledger Items Table
            PdfPTable itemsTable = new PdfPTable(3);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingAfter(30);
            itemsTable.setWidths(new float[]{4f, 2f, 2f});

            // Headers
            PdfPCell h1 = new PdfPCell(new Phrase("Description", boldFont));
            h1.setBackgroundColor(new Color(240, 240, 240));
            h1.setPadding(8);
            itemsTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Duration", boldFont));
            h2.setBackgroundColor(new Color(240, 240, 240));
            h2.setPadding(8);
            itemsTable.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Price (USD)", boldFont));
            h3.setBackgroundColor(new Color(240, 240, 240));
            h3.setPadding(8);
            itemsTable.addCell(h3);

            // Product Details row
            MembershipPlan plan = payment.getMembershipPlan();
            PdfPCell d1 = new PdfPCell(new Phrase(plan != null ? plan.getName() : "Gym Subscription Plan", regularFont));
            d1.setPadding(8);
            itemsTable.addCell(d1);

            PdfPCell d2 = new PdfPCell(new Phrase(plan != null ? plan.getDurationMonths() + " Month(s)" : "N/A", regularFont));
            d2.setPadding(8);
            itemsTable.addCell(d2);

            PdfPCell d3 = new PdfPCell(new Phrase("₹" + payment.getAmount().toString(), regularFont));
            d3.setPadding(8);
            itemsTable.addCell(d3);

            document.add(itemsTable);

            // Summary Totals
            Paragraph totalText = new Paragraph("Total Charged: ₹" + payment.getAmount().toString(), boldFont);
            totalText.setAlignment(Element.ALIGN_RIGHT);
            totalText.setSpacingAfter(30);
            document.add(totalText);

            // Closing Footer
            Paragraph footer = new Paragraph("Thank you for training with GYM FIT CENTRE! We appreciate your commitment to your health goals.", boldFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount().doubleValue());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setStatus(payment.getStatus());
        dto.setPdfReceiptPath(payment.getPdfReceiptPath());

        if (payment.getMember() != null) {
            dto.setMemberId(payment.getMember().getId());
            if (payment.getMember().getUser() != null) {
                dto.setMemberName(payment.getMember().getUser().getFullName());
                dto.setMemberEmail(payment.getMember().getUser().getEmail());
            }
        }

        if (payment.getMembershipPlan() != null) {
            dto.setMembershipPlanId(payment.getMembershipPlan().getId());
            dto.setMembershipPlanName(payment.getMembershipPlan().getName());
        }

        return dto;
    }
}
