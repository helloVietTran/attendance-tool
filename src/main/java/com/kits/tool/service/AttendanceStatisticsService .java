package com.kits.tool.service;

import com.kits.tool.dto.WorkTimeStatDTO;
import com.kits.tool.entity.DailyWorkTimeAnalysis;
import com.kits.tool.repository.DailyWorkTimeAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WorkTimeAnalysisService {

    @Autowired
    private DailyWorkTimeAnalysisRepository repo;

    public List<DailyWorkTimeAnalysis> getData(LocalDate start, LocalDate end) {
        return repo.findByWorkDateBetween(start, end);
    }

    public String exportExcel(LocalDate start, LocalDate end) {
        try {
            List<DailyWorkTimeAnalysis> list = getData(start, end);

            String folder = "src/main/resources/output/";
            Files.createDirectories(Paths.get(folder));

            String fileName = folder +
                    "statistics_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                    ".xlsx";

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Statistics");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Employee ID", "Date", "Late", "Early Leave",
                    "Lack", "OT", "In Office", "Work Time",
                    "Shift Start", "Shift End"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Data rows
            int rowIdx = 1;
            for (DailyWorkTimeAnalysis d : list) {
                Row r = sheet.createRow(rowIdx++);

                r.createCell(0).setCellValue(d.getEmployeeId());
                r.createCell(1).setCellValue(d.getWorkDate().toString());
                r.createCell(2).setCellValue(d.getLateMinutes());
                r.createCell(3).setCellValue(d.getEarlyLeaveMinutes());
                r.createCell(4).setCellValue(d.getLackMinutes());
                r.createCell(5).setCellValue(d.getOverTimeMinutes());
                r.createCell(6).setCellValue(d.getInOfficeMinutes());
                r.createCell(7).setCellValue(d.getWorkTimeMinutes());
                r.createCell(8).setCellValue(d.getShiftStartTime().toString());
                r.createCell(9).setCellValue(d.getShiftEndTime().toString());
            }

            // Autosize
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream out = new FileOutputStream(fileName);
            wb.write(out);
            out.close();
            wb.close();

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error while exporting Excel", e);
        }
    }
}
