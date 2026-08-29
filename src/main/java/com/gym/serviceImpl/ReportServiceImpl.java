package com.gym.serviceImpl;

import com.gym.entity.*;
import com.gym.exception.BadRequestException;
import com.gym.repository.AttendanceRepository;
import com.gym.repository.MemberRepository;
import com.gym.repository.PaymentRepository;

import com.gym.service.ReportService;
import java.util.stream.Collectors;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.PageSize;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private com.gym.repository.TrainerRepository trainerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public ByteArrayInputStream exportCsv(String type) {
        StringBuilder sb = new StringBuilder();

        switch (type.toLowerCase()) {
            case "members":
                sb.append("ID,Full Name,Username,Email,Phone,Plan,Expiry Date,Status\n");
                List<Member> members = memberRepository.findAll();
                for (Member m : members) {
                    sb.append(m.getId()).append(",")
                      .append(escapeCsv(m.getUser().getFullName())).append(",")
                      .append(escapeCsv(m.getUser().getUsername())).append(",")
                      .append(escapeCsv(m.getUser().getEmail())).append(",")
                      .append(escapeCsv(m.getUser().getPhone())).append(",")
                      .append(escapeCsv(m.getMembershipPlan() != null ? m.getMembershipPlan().getName() : "None")).append(",")
                      .append(m.getMembershipExpiryDate() != null ? m.getMembershipExpiryDate().toString() : "N/A").append(",")
                      .append(m.getStatus()).append("\n");
                }
                break;

            case "trainers":
                sb.append("ID,Full Name,Username,Email,Phone,Specialization,Experience (Years),Salary,Status\n");
                List<Trainer> trainersCsv = trainerRepository.findAll();
                for (Trainer t : trainersCsv) {
                    sb.append(t.getId()).append(",")
                      .append(escapeCsv(t.getUser().getFullName())).append(",")
                      .append(escapeCsv(t.getUser().getUsername())).append(",")
                      .append(escapeCsv(t.getUser().getEmail())).append(",")
                      .append(escapeCsv(t.getUser().getPhone())).append(",")
                      .append(escapeCsv(t.getSpecialization())).append(",")
                      .append(t.getExperienceYears()).append(",")
                      .append(t.getSalary()).append(",")
                      .append(t.getStatus()).append("\n");
                }
                break;

            case "payments":
                sb.append("ID,Member Name,Plan,Amount (USD),Payment Date,Method,Transaction ID,Status\n");
                List<Payment> payments = paymentRepository.findAll();
                for (Payment p : payments) {
                    sb.append(p.getId()).append(",")
                      .append(escapeCsv(p.getMember().getUser().getFullName())).append(",")
                      .append(escapeCsv(p.getMembershipPlan() != null ? p.getMembershipPlan().getName() : "None")).append(",")
                      .append(p.getAmount()).append(",")
                      .append(p.getPaymentDate().format(DATE_FORMAT)).append(",")
                      .append(escapeCsv(p.getPaymentMethod())).append(",")
                      .append(escapeCsv(p.getTransactionId())).append(",")
                      .append(p.getStatus()).append("\n");
                }
                break;

            case "attendance":
                sb.append("ID,Full Name,Role,Date,Check-In,Check-Out,Status\n");
                List<Attendance> attendance = attendanceRepository.findAll();
                for (Attendance a : attendance) {
                    sb.append(a.getId()).append(",")
                      .append(escapeCsv(a.getUser().getFullName())).append(",")
                      .append(escapeCsv(a.getUser().getRoles().stream().map(Role::getName).collect(Collectors.joining("|")))).append(",")
                      .append(a.getDate().format(DATE_FORMAT)).append(",")
                      .append(a.getCheckInTime() != null ? a.getCheckInTime().format(TIME_FORMAT) : "").append(",")
                      .append(a.getCheckOutTime() != null ? a.getCheckOutTime().format(TIME_FORMAT) : "").append(",")
                      .append(a.getStatus()).append("\n");
                }
                break;

            case "revenue":
                sb.append("Plan Name,Duration (Months),Price,Total Active Subscriptions,Estimated Monthly Yield\n");
                List<Member> allM = memberRepository.findAll();
                Map<String, Integer> planCount = new HashMap<>();
                for (Member m : allM) {
                    if ("ACTIVE".equalsIgnoreCase(m.getStatus()) && m.getMembershipPlan() != null) {
                        String planName = m.getMembershipPlan().getName();
                        planCount.put(planName, planCount.getOrDefault(planName, 0) + 1);
                    }
                }
                for (MembershipPlan plan : memberRepository.findAll().stream().map(Member::getMembershipPlan).filter(p -> p != null).distinct().collect(Collectors.toList())) {
                    int count = planCount.getOrDefault(plan.getName(), 0);
                    double yield = count * plan.getPrice().doubleValue() / plan.getDurationMonths();
                    sb.append(escapeCsv(plan.getName())).append(",")
                      .append(plan.getDurationMonths()).append(",")
                      .append(plan.getPrice()).append(",")
                      .append(count).append(",")
                      .append(yield).append("\n");
                }
                break;

            default:
                throw new BadRequestException("Invalid report type: " + type);
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Override
    public ByteArrayInputStream exportExcel(String type) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(type + " Report");

            // Fonts & Styles
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Columns and Data population
            String[] columns;
            List<Member> members;
            List<Payment> payments;
            List<Attendance> attendance;

            switch (type.toLowerCase()) {
                case "members":
                    columns = new String[]{"ID", "Full Name", "Username", "Email", "Phone", "Plan", "Expiry Date", "Status"};
                    Row headerRow = sheet.createRow(0);
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }

                    members = memberRepository.findAll();
                    int rowIdx = 1;
                    for (Member m : members) {
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(m.getId());
                        row.createCell(1).setCellValue(m.getUser().getFullName());
                        row.createCell(2).setCellValue(m.getUser().getUsername());
                        row.createCell(3).setCellValue(m.getUser().getEmail());
                        row.createCell(4).setCellValue(m.getUser().getPhone() != null ? m.getUser().getPhone() : "");
                        row.createCell(5).setCellValue(m.getMembershipPlan() != null ? m.getMembershipPlan().getName() : "None");
                        row.createCell(6).setCellValue(m.getMembershipExpiryDate() != null ? m.getMembershipExpiryDate().toString() : "N/A");
                        row.createCell(7).setCellValue(m.getStatus());
                    }
                    break;

                case "trainers":
                    columns = new String[]{"ID", "Full Name", "Username", "Email", "Phone", "Specialization", "Experience (Years)", "Salary", "Status"};
                    Row tRow = sheet.createRow(0);
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = tRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }

                    List<Trainer> trainersList = trainerRepository.findAll();
                    int tRowIdx = 1;
                    for (Trainer t : trainersList) {
                        Row row = sheet.createRow(tRowIdx++);
                        row.createCell(0).setCellValue(t.getId());
                        row.createCell(1).setCellValue(t.getUser().getFullName());
                        row.createCell(2).setCellValue(t.getUser().getUsername());
                        row.createCell(3).setCellValue(t.getUser().getEmail());
                        row.createCell(4).setCellValue(t.getUser().getPhone() != null ? t.getUser().getPhone() : "");
                        row.createCell(5).setCellValue(t.getSpecialization() != null ? t.getSpecialization() : "");
                        row.createCell(6).setCellValue(t.getExperienceYears() != null ? t.getExperienceYears() : 0);
                        row.createCell(7).setCellValue(t.getSalary() != null ? t.getSalary().doubleValue() : 0.0);
                        row.createCell(8).setCellValue(t.getStatus());
                    }
                    break;

                case "payments":
                    columns = new String[]{"ID", "Member Name", "Plan", "Amount (INR)", "Payment Date", "Method", "Transaction ID", "Status"};
                    Row pRow = sheet.createRow(0);
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = pRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }

                    payments = paymentRepository.findAll();
                    int pRowIdx = 1;
                    for (Payment p : payments) {
                        Row row = sheet.createRow(pRowIdx++);
                        row.createCell(0).setCellValue(p.getId());
                        row.createCell(1).setCellValue(p.getMember().getUser().getFullName());
                        row.createCell(2).setCellValue(p.getMembershipPlan() != null ? p.getMembershipPlan().getName() : "None");
                        row.createCell(3).setCellValue(p.getAmount().doubleValue());
                        row.createCell(4).setCellValue(p.getPaymentDate().format(DATE_FORMAT));
                        row.createCell(5).setCellValue(p.getPaymentMethod());
                        row.createCell(6).setCellValue(p.getTransactionId());
                        row.createCell(7).setCellValue(p.getStatus());
                    }
                    break;

                case "attendance":
                    columns = new String[]{"ID", "Full Name", "Role", "Date", "Check-In", "Check-Out", "Status"};
                    Row aRow = sheet.createRow(0);
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = aRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }

                    attendance = attendanceRepository.findAll();
                    int aRowIdx = 1;
                    for (Attendance a : attendance) {
                        Row row = sheet.createRow(aRowIdx++);
                        row.createCell(0).setCellValue(a.getId());
                        row.createCell(1).setCellValue(a.getUser().getFullName());
                        row.createCell(2).setCellValue(a.getUser().getRoles().isEmpty() ? "N/A" : a.getUser().getRoles().iterator().next().getName());
                        row.createCell(3).setCellValue(a.getDate().toString());
                        row.createCell(4).setCellValue(a.getCheckInTime() != null ? a.getCheckInTime().format(TIME_FORMAT) : "N/A");
                        row.createCell(5).setCellValue(a.getCheckOutTime() != null ? a.getCheckOutTime().format(TIME_FORMAT) : "N/A");
                        row.createCell(6).setCellValue(a.getStatus());
                    }
                    break;

                case "revenue":
                    columns = new String[]{"Plan Name", "Duration (Months)", "Price", "Total Active Subscriptions", "Estimated Monthly Yield"};
                    Row rRow = sheet.createRow(0);
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = rRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerCellStyle);
                    }

                    List<Member> allM = memberRepository.findAll();
                    Map<String, Integer> planCount = new HashMap<>();
                    for (Member m : allM) {
                        if ("ACTIVE".equalsIgnoreCase(m.getStatus()) && m.getMembershipPlan() != null) {
                            String planName = m.getMembershipPlan().getName();
                            planCount.put(planName, planCount.getOrDefault(planName, 0) + 1);
                        }
                    }

                    int rRowIdx = 1;
                    List<MembershipPlan> distinctPlans = memberRepository.findAll().stream().map(Member::getMembershipPlan).filter(p -> p != null).distinct().collect(Collectors.toList());
                    for (MembershipPlan plan : distinctPlans) {
                        int count = planCount.getOrDefault(plan.getName(), 0);
                        double yield = count * plan.getPrice().doubleValue() / plan.getDurationMonths();

                        Row row = sheet.createRow(rRowIdx++);
                        row.createCell(0).setCellValue(plan.getName());
                        row.createCell(1).setCellValue(plan.getDurationMonths());
                        row.createCell(2).setCellValue(plan.getPrice().doubleValue());
                        row.createCell(3).setCellValue(count);
                        row.createCell(4).setCellValue(yield);
                    }
                    break;

                default:
                    throw new BadRequestException("Invalid report type: " + type);
            }

            // Autosize columns
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel sheet", e);
        }
    }

    @Override
    public ByteArrayInputStream exportPdf(String type) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape is better for wide reports
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Typography styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(33, 37, 41));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph title = new Paragraph(type.toUpperCase() + " SYSTEM REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table;

            switch (type.toLowerCase()) {
                case "members":
                    table = new PdfPTable(8);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1f, 2.5f, 2f, 3f, 2f, 2f, 2f, 1.5f});

                    addPdfHeader(table, headerFont, new String[]{"ID", "Full Name", "Username", "Email", "Phone", "Plan", "Expiry Date", "Status"});

                    for (Member m : memberRepository.findAll()) {
                        table.addCell(new PdfPCell(new Phrase(m.getId().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getUser().getFullName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getUser().getUsername(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getUser().getEmail(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getUser().getPhone() != null ? m.getUser().getPhone() : "", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getMembershipPlan() != null ? m.getMembershipPlan().getName() : "None", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getMembershipExpiryDate() != null ? m.getMembershipExpiryDate().toString() : "N/A", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(m.getStatus(), cellFont)));
                    }
                    break;

                case "trainers":
                    table = new PdfPTable(9);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1f, 2.5f, 2f, 3f, 2f, 2.5f, 1.5f, 1.5f, 1.5f});

                    addPdfHeader(table, headerFont, new String[]{"ID", "Full Name", "Username", "Email", "Phone", "Specialization", "Exp (Yrs)", "Salary", "Status"});

                    for (Trainer t : trainerRepository.findAll()) {
                        table.addCell(new PdfPCell(new Phrase(t.getId().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getUser().getFullName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getUser().getUsername(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getUser().getEmail(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getUser().getPhone() != null ? t.getUser().getPhone() : "", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getSpecialization() != null ? t.getSpecialization() : "", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(String.valueOf(t.getExperienceYears() != null ? t.getExperienceYears() : 0), cellFont)));
                        table.addCell(new PdfPCell(new Phrase("₹" + (t.getSalary() != null ? t.getSalary().toString() : "0"), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(t.getStatus(), cellFont)));
                    }
                    break;

                case "payments":
                    table = new PdfPTable(8);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1f, 2.5f, 2.5f, 1.5f, 2f, 2f, 2.5f, 1.5f});

                    addPdfHeader(table, headerFont, new String[]{"ID", "Member Name", "Plan Name", "Amount", "Date", "Method", "Transaction ID", "Status"});

                    for (Payment p : paymentRepository.findAll()) {
                        table.addCell(new PdfPCell(new Phrase(p.getId().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getMember().getUser().getFullName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getMembershipPlan() != null ? p.getMembershipPlan().getName() : "None", cellFont)));
                        table.addCell(new PdfPCell(new Phrase("₹" + p.getAmount().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getPaymentDate().format(DATE_FORMAT), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getPaymentMethod(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getTransactionId(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getStatus(), cellFont)));
                    }
                    break;

                case "attendance":
                    table = new PdfPTable(7);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{1f, 3f, 2f, 2f, 2f, 2f, 1.5f});

                    addPdfHeader(table, headerFont, new String[]{"ID", "Full Name", "Role", "Date", "Check-In", "Check-Out", "Status"});

                    for (Attendance a : attendanceRepository.findAll()) {
                        table.addCell(new PdfPCell(new Phrase(a.getId().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getUser().getFullName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getUser().getRoles().isEmpty() ? "N/A" : a.getUser().getRoles().iterator().next().getName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getDate().format(DATE_FORMAT), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getCheckInTime() != null ? a.getCheckInTime().format(TIME_FORMAT) : "N/A", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getCheckOutTime() != null ? a.getCheckOutTime().format(TIME_FORMAT) : "N/A", cellFont)));
                        table.addCell(new PdfPCell(new Phrase(a.getStatus(), cellFont)));
                    }
                    break;

                case "revenue":
                    table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[]{3f, 2f, 2f, 2f, 2.5f});

                    addPdfHeader(table, headerFont, new String[]{"Plan Name", "Duration (Months)", "Price", "Active Subscriptions", "Est. Monthly Yield"});

                    List<Member> allM = memberRepository.findAll();
                    Map<String, Integer> planCount = new HashMap<>();
                    for (Member m : allM) {
                        if ("ACTIVE".equalsIgnoreCase(m.getStatus()) && m.getMembershipPlan() != null) {
                            String planName = m.getMembershipPlan().getName();
                            planCount.put(planName, planCount.getOrDefault(planName, 0) + 1);
                        }
                    }

                    List<MembershipPlan> distinctPlans = memberRepository.findAll().stream().map(Member::getMembershipPlan).filter(p -> p != null).distinct().collect(Collectors.toList());
                    for (MembershipPlan plan : distinctPlans) {
                        int count = planCount.getOrDefault(plan.getName(), 0);
                        double yield = count * plan.getPrice().doubleValue() / plan.getDurationMonths();

                        table.addCell(new PdfPCell(new Phrase(plan.getName(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(plan.getDurationMonths() + " Month(s)", cellFont)));
                        table.addCell(new PdfPCell(new Phrase("₹" + plan.getPrice().toString(), cellFont)));
                        table.addCell(new PdfPCell(new Phrase(String.valueOf(count), cellFont)));
                        table.addCell(new PdfPCell(new Phrase("₹" + String.format("%.2f", yield), cellFont)));
                    }
                    break;

                default:
                    throw new BadRequestException("Invalid report type: " + type);
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addPdfHeader(PdfPTable table, Font font, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new Color(27, 38, 59));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
}
