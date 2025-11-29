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
import com.kits.tool.service.LogService;
import com.kits.tool.service.HolidayImportService;
import com.kits.tool.service.PayrollClosingService;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;


@SpringBootApplication
public class ToolApplication{
	public static void main(String[] args) {
		SpringApplication.run(ToolApplication.class, args);
	}
}
