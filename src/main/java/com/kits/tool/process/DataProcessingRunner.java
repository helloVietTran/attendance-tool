package com.kits.tool.process;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kits.tool.dto.*;
import com.kits.tool.service.*;
import com.kits.tool.service.excel.AttendanceTransformer;
import com.kits.tool.service.excel.ExcelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataProcessingRunner implements CommandLineRunner {

    @Autowired
    LogService logService;

    @Autowired
    PayrollClosingService payrollClosingService;

    @Autowired
    HolidayImportService holidayImportService;

    @Autowired
    CalendarService calendarService;

    @Autowired
    DailyWorkTimeAnalysisService dailyWorkTimeAnalysisService;

    @Override
    public void run(String... args) throws Exception {

        /*================================= Xử lý chấm công =================================*/

        ClassPathResource resource = new ClassPathResource("input/cham-cong.xlsx");
        List<ExcelRowDTO> rawData;

        try (InputStream is = resource.getInputStream()) {
//            ExcelListener listener = new ExcelListener();
            rawData = EasyExcel.read(is, ExcelRowDTO.class, null)
                    .head(ExcelRowDTO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
        }
        List<ExcelExportDTO> export = AttendanceTransformer.transform(rawData);
        logService.saveRawFromExcel(rawData);
        logService.saveNewFromExcel(export);

        /*================================= Lịch chốt công =================================*/

        System.out.println("Payroll process started...");

        ClassPathResource resourcePayroll = new ClassPathResource("input/lich-chot-cong.xlsx");
        List<YearMonthClosingDTO> excelRows;

        try (InputStream is = resourcePayroll.getInputStream()) {
            excelRows = EasyExcel.read(is, YearMonthClosingDTO.class, null)
                    .headRowNumber(2)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
        }

        List<DateClosingDTO> result = payrollClosingService.fromRawtoDate(excelRows);
        payrollClosingService.saveToDb(result);

        /*================================= Ngày nghỉ =================================*/

        holidayImportService.importFromExcel(new ClassPathResource("input/public_holidays.xlsx"));
        holidayImportService.importFromExcel(new ClassPathResource("input/other_holidays.xlsx"));

        holidayImportService.generateWeekendHolidays(LocalDate.now().getYear());

        /*================================= Phân tích thời gian làm việc =================================*/

        System.out.println("Daily work time analysis started...");
        dailyWorkTimeAnalysisService.processAllDailyWorkTime();
        System.out.println("Daily work time analysis completed!");
    }
}
