package com.gym.controller;

import com.gym.exception.BadRequestException;
import com.gym.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/admin/reports/export/{format}/{type}")
    public ResponseEntity<InputStreamResource> exportReport(@PathVariable String format, @PathVariable String type) {
        ByteArrayInputStream bis;
        String contentType;
        String extension;

        if ("pdf".equalsIgnoreCase(format)) {
            bis = reportService.exportPdf(type);
            contentType = "application/pdf";
            extension = ".pdf";
        } else if ("excel".equalsIgnoreCase(format)) {
            bis = reportService.exportExcel(type);
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            extension = ".xlsx";
        } else if ("csv".equalsIgnoreCase(format)) {
            bis = reportService.exportCsv(type);
            contentType = "text/csv";
            extension = ".csv";
        } else {
            throw new BadRequestException("Unsupported export format: " + format);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + type + "_report" + extension);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(bis));
    }
}
