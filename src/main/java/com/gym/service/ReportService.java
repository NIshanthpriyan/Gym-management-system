package com.gym.service;

import java.io.ByteArrayInputStream;

public interface ReportService {
    ByteArrayInputStream exportCsv(String type);
    ByteArrayInputStream exportExcel(String type);
    ByteArrayInputStream exportPdf(String type);
}
