package com.kits.tool.excel_process;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kits.tool.dto.attendancedto.ExcelExportDTO;
import com.kits.tool.dto.attendancedto.ExcelRowDTO;
import com.kits.tool.dto.payrolldto.DateClosingDTO;
import com.kits.tool.dto.payrolldto.YearMonthClosingDTO;
import com.kits.tool.service.CalendarService;
import com.kits.tool.service.LogService;
import com.kits.tool.service.HolidayImportService;
import com.kits.tool.service.PayrollClosingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Component
public class ToolMain implements CommandLineRunner {
    @Autowired
    LogService logService;
    @Autowired
    PayrollClosingService payrollClosingService;
    @Autowired
    HolidayImportService holidayImportService;
    @Autowired
    CalendarService calendarService;

    public void run(String... args) throws Exception {

    //Xử lý lưu log file vào db
        //Đọc file
        ClassPathResource resource  = new ClassPathResource("input/cham-cong.xlsx");
        List<ExcelRowDTO> rawData; //Lưu data gốc

        try(InputStream is = resource.getInputStream()) {
            //Check đọc file: in ra console với listener
            ExcelListener listener = new ExcelListener();
            rawData = EasyExcel.read(is, ExcelRowDTO.class, listener)
                                .head(ExcelRowDTO.class)
                                .excelType(ExcelTypeEnum.XLSX)
                                .sheet()
                                .doReadSync();
        }


        //Chuyển đổi format gốc sang format mới
        List<ExcelExportDTO> export = AttendanceTransformer.transform(rawData);

        //Lưu vào db dưới dạng format gốc
        logService.saveRawFromExcel(rawData);

        //Lưu vào db dưới dạng format mới
        logService.saveNewFromExcel(export);

//        // Đường dẫn tới thư mục resources
//        String path = Paths.get("src", "main", "resources", "output", "attendance.xlsx").toString();
//        //Lưu ra file excel mới đặt trong trong thư mục output
//        EasyExcel.write(path, ExcelExportDTO.class)
//                .sheet()
//                .doWrite(export);
//        System.out.println("Đã ghi ra file: " + path);

/*====================================================================================================================*/

    //Xử lý lưu lịch chốt công vào db
        System.out.println("Payroll process run() started");

        List<DateClosingDTO> result;
        List<YearMonthClosingDTO> excelRows;

        ClassPathResource resourcePayrollClosing  = new ClassPathResource("input/lich-chot-cong.xlsx");

        try(InputStream is = resourcePayrollClosing.getInputStream()) {
            excelRows = EasyExcel.read(
                            is,
                            YearMonthClosingDTO.class,
                            null)
                    .headRowNumber(2)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .doReadSync();
        }

        //Thực hiện chuyển từ data gốc sang format data mới
        result = payrollClosingService.fromRawtoDate(excelRows);

        //Lưu data chốt công vào db
        payrollClosingService.saveToDb(result);

//        // Đường dẫn tới thư mục resources
//        String pathPayroll = Paths.get(
//                "src", "main", "resources", "output", "payroll_closing.xlsx")
//                .toString();
//        //Lưu ra file excel mới đặt trong trong thư mục output
//        EasyExcel.write(pathPayroll, DateClosingDTO.class)
//                .sheet()
//                .doWrite(result);
//        System.out.println("Đã ghi ra file: " + pathPayroll);

/*====================================================================================================================*/

    //Xử lý lưu các ngày nghỉ vào db
        ClassPathResource resourcePublic  = new ClassPathResource("input/public_holidays.xlsx");

        ClassPathResource resourceOthers  = new ClassPathResource("input/other_holidays.xlsx");

        //Lưu data file public_holidays
        holidayImportService.importFromExcel(resourcePublic);
        //Lưu data file other_holidays
        holidayImportService.importFromExcel(resourceOthers);

        //Lưu các ngày cuối tuần thứ 7, chủ nhật
        int currentYear = LocalDate.now().getYear();
        holidayImportService.generateWeekendHolidays(currentYear);
    }
}
