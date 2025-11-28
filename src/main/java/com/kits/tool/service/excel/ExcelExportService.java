package com.example.attendance.service;

import com.example.attendance.dto.AttendanceStatistics;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class ExcelExportService {

    private static final String OUTPUT_DIR = "src/main/resources/output";
    private static final int MAX_FILES_PER_TYPE = 5; // Giữ tối đa 5 file mỗi loại
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Xuất thống kê ra file Excel
     */
    public String exportToExcel(List<AttendanceStatistics> statistics, String reportType) {
        if (statistics == null || statistics.isEmpty()) {
            log.warn("Không có dữ liệu để xuất Excel");
            return null;
        }

        try {
            // Tạo thư mục output nếu chưa có
            createOutputDirectory();

            // Tạo filename
            String fileName = generateFileName(reportType, statistics.get(0).getPeriod());
            String filePath = OUTPUT_DIR + File.separator + fileName;

            // Tạo workbook
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Thống kê chấm công");

                // Tạo styles
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle numberStyle = createNumberStyle(workbook);

                // Tạo header
                createHeader(sheet, headerStyle);

                // Fill dữ liệu
                fillData(sheet, statistics, dataStyle, numberStyle);

                // Auto-size columns
                for (int i = 0; i < 14; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Ghi file
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                }

                log.info("Đã xuất file Excel thành công: {}", filePath);
            }

            // Dọn dẹp file cũ
            cleanupOldFiles(reportType);

            return filePath;

        } catch (IOException e) {
            log.error("Lỗi khi xuất file Excel", e);
            return null;
        }
    }

    /**
     * Tạo thư mục output
     */
    private void createOutputDirectory() throws IOException {
        Path path = Paths.get(OUTPUT_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Đã tạo thư mục output: {}", OUTPUT_DIR);
        }
    }

    /**
     * Tạo tên file
     */
    private String generateFileName(String reportType, String period) {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        return String.format("ChamCong_%s_%s_%s.xlsx",
                reportType, period.replace("/", "-"), timestamp);
    }

    /**
     * Tạo header cho Excel
     */
    private void createHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Mã NV", "Kỳ", "Số ngày làm",
                "Tổng phút đi muộn", "Tổng phút về sớm", "Tổng phút thiếu",
                "Tổng phút OT", "Tổng phút trong văn phòng", "Tổng phút làm việc",
                "TB đi muộn (phút)", "TB về sớm (phút)", "TB giờ làm",
                "Số ngày đi muộn", "Số ngày về sớm", "Số ngày OT"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Fill dữ liệu vào sheet
     */
    private void fillData(Sheet sheet, List<AttendanceStatistics> statistics,
                          CellStyle dataStyle, CellStyle numberStyle) {
        int rowNum = 1;

        for (AttendanceStatistics stat : statistics) {
            Row row = sheet.createRow(rowNum++);

            createCell(row, 0, stat.getEmployeeId(), dataStyle);
            createCell(row, 1, stat.getPeriod(), dataStyle);
            createCell(row, 2, stat.getTotalWorkDays(), numberStyle);
            createCell(row, 3, stat.getTotalLateMinutes(), numberStyle);
            createCell(row, 4, stat.getTotalEarlyLeaveMinutes(), numberStyle);
            createCell(row, 5, stat.getTotalLackMinutes(), numberStyle);
            createCell(row, 6, stat.getTotalOverTimeMinutes(), numberStyle);
            createCell(row, 7, stat.getTotalInOfficeMinutes(), numberStyle);
            createCell(row, 8, stat.getTotalWorkTimeMinutes(), numberStyle);
            createCell(row, 9, String.format("%.1f", stat.getAvgLateMinutes()), numberStyle);
            createCell(row, 10, String.format("%.1f", stat.getAvgEarlyLeaveMinutes()), numberStyle);
            createCell(row, 11, String.format("%.2f", stat.getAvgWorkHours()), numberStyle);
            createCell(row, 12, stat.getLateDaysCount(), numberStyle);
            createCell(row, 13, stat.getEarlyLeaveDaysCount(), numberStyle);
            createCell(row, 14, stat.getOverTimeDaysCount(), numberStyle);
        }
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    /**
     * Tạo style cho header
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Tạo style cho data
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Tạo style cho số
     */
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    /**
     * Dọn dẹp file cũ, chỉ giữ lại MAX_FILES_PER_TYPE file mới nhất của mỗi loại
     */
    private void cleanupOldFiles(String reportType) {
        try {
            File outputDir = new File(OUTPUT_DIR);
            if (!outputDir.exists()) return;

            // Lọc file theo reportType
            File[] files = outputDir.listFiles((dir, name) ->
                    name.startsWith("ChamCong_" + reportType) && name.endsWith(".xlsx"));

            if (files == null || files.length <= MAX_FILES_PER_TYPE) {
                return;
            }

            // Sắp xếp theo thời gian sửa đổi (mới nhất trước)
            Stream.of(files)
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .skip(MAX_FILES_PER_TYPE)
                    .forEach(file -> {
                        if (file.delete()) {
                            log.info("Đã xóa file cũ: {}", file.getName());
                        }
                    });

        } catch (Exception e) {
            log.error("Lỗi khi dọn dẹp file cũ", e);
        }
    }
}