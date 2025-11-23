package com.kits.tool.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDataImporter {
    private static final Logger logger = LogManager.getLogger(EmployeeDataImporter.class);

    /**
     * Đọc file Excel chứa ID và Email nhân viên.
     * @param filePath Đường dẫn đến file Excel.
     * @return Map<EmployeeID, EmployeeEmail>
     */
    public Map<String, String> importEmployeeEmails(String filePath) {
        Map<String, String> employeeMap = new HashMap<>();

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Đang đọc file Employee Data: {}", filePath);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String employeeId = getCellValueAsString(row.getCell(0));
                    String email = row.getCell(1).getStringCellValue();

                    if (!employeeId.isEmpty() && email.contains("@")) {
                        employeeMap.put(employeeId, email);
                    }
                } catch (Exception e) {
                    logger.error("Lỗi đọc dữ liệu nhân viên tại dòng {}: {}", i, e.getMessage());
                }
            }
            logger.info("Tải thành công {} địa chỉ email.", employeeMap.size());
        } catch (IOException e) {
            logger.error("Lỗi I/O khi đọc file Employee Data: {}", e.getMessage(), e);
        }
        return employeeMap;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }
}
