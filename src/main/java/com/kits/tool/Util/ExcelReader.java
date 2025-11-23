package com.kits.tool.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelReader {
    private static final Logger logger = LogManager.getLogger(ExcelReader.class);

    /**
     * Hàm đọc dữ liệu chấm công từ file Excel.
     * @param filePath Đường dẫn đến file Excel.
     * @return List các bản ghi TimesheetRecord.
     */
    public List<TimesheetRecord> readLogData(String filePath) {
        List<TimesheetRecord> records = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) { // Hoặc HSSFWorkbook cho .xls

            Sheet firstSheet = workbook.getSheetAt(0);
            logger.info("Đang đọc dữ liệu từ file: {}", filePath);

            for (int i = 1; i <= firstSheet.getLastRowNum(); i++) { // Bắt đầu từ hàng 1 (sau header)
                Row row = firstSheet.getRow(i);
                if (row == null) continue;

                TimesheetRecord record = new TimesheetRecord();

                // Giả định: Cột 0 là ID, Cột 1 là TimeStamp
                record.employeeId = getCellValueAsString(row.getCell(0));
                record.timeStamp = row.getCell(1).getDateCellValue();

                records.add(record);
            }
            logger.info("Đọc thành công {} bản ghi.", records.size());
        } catch (IOException | IllegalStateException e) {
            logger.error("Lỗi khi đọc file Excel: {}", filePath, e);
        }
        return records;
    }

    // Hàm phụ trợ để xử lý ô là số nhưng chứa ID
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }
}