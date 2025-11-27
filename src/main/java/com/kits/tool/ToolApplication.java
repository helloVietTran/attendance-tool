package com.kits.tool;

import com.kits.tool.service.excel.AttendanceTransformer;
import com.kits.tool.service.excel.ExcelListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.dto.DateClosingDTO;
import com.kits.tool.dto.YearMonthClosingDTO;
import com.kits.tool.service.CalendarService;
import com.kits.tool.service.ForgotCheckoutNotificationService;
import com.kits.tool.service.LogService;
import com.kits.tool.service.HolidayImportService;
import com.kits.tool.service.PayrollClosingService;
import com.kits.tool.service.WorkTimeAnalysisService;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;


@SpringBootApplication
public class ToolApplication implements CommandLineRunner {
	@Autowired
	LogService logService;

	@Autowired
	PayrollClosingService payrollClosingService;

	@Autowired
	HolidayImportService holidayImportService;

	@Autowired
	CalendarService calendarService;

	@Autowired
	WorkTimeAnalysisService workTimeAnalysisService;

	@Autowired
	ForgotCheckoutNotificationService forgotCheckoutNotificationService;

	@Override
	public void run(String... args) throws Exception {

		/*================================= Xử lý chấm công =================================*/

		ClassPathResource resource = new ClassPathResource("input/cham-cong.xlsx");
		List<ExcelRowDTO> rawData;

		try (InputStream is = resource.getInputStream()) {
			//Check đọc file: in ra console với listener
			ExcelListener listener = new ExcelListener();
			rawData = EasyExcel.read(is, ExcelRowDTO.class, listener)
					.head(ExcelRowDTO.class)
					.excelType(ExcelTypeEnum.XLSX)
					.sheet()
					.doReadSync();
		}

		//Chuyển đổi format gốc sang format mới => không cần validate
		List<ExcelExportDTO> export = AttendanceTransformer.transform(rawData);

		logService.saveRawFromExcel(rawData);//Lưu vào db dưới dạng format gốc
		logService.saveNewFromExcel(export);//Lưu vào db dưới dạng format mới
		
		// Phân tích thời gian làm việc
		workTimeAnalysisService.analyzeAndSave(export);
		System.out.println("✅ Đã phân tích và lưu dữ liệu vào DailyWorkTimeAnalysis");

		// Phát hiện và lưu log quên checkout
		forgotCheckoutNotificationService.detectForgotCheckout(rawData, export);
		
		// Gửi email cảnh báo (tối đa 3 lần, mỗi ngày 1 lần)
		forgotCheckoutNotificationService.sendPendingNotifications(rawData);
		System.out.println("✅ Đã xử lý email cảnh báo quên checkout");

		/*================================= Xử lý lịch chốt công =================================*/

		System.out.println("Payroll process run() started");

		List<YearMonthClosingDTO> excelRows;

		ClassPathResource resourcePayrollClosing = new ClassPathResource("input/lich-chot-cong.xlsx");
		try (InputStream is = resourcePayrollClosing.getInputStream()) {
			excelRows = EasyExcel.read(is, YearMonthClosingDTO.class, null)
					.headRowNumber(2)
					.excelType(ExcelTypeEnum.XLSX)
					.sheet()
					.doReadSync();
		}

		List<DateClosingDTO> result = payrollClosingService.fromRawtoDate(excelRows);
		payrollClosingService.saveToDb(result);

		/*================================= Xử lý file ngày nghỉ =================================*/

		holidayImportService.importFromExcel(new ClassPathResource("input/public_holidays.xlsx"));
		holidayImportService.importFromExcel(new ClassPathResource("input/other_holidays.xlsx"));

		//Lưu các ngày cuối tuần thứ 7, chủ nhật
		int currentYear = LocalDate.now().getYear();
		holidayImportService.generateWeekendHolidays(currentYear);
	}

	public static void main(String[] args) {
		SpringApplication.run(ToolApplication.class, args);
	}
}
