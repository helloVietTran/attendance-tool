package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class ExcelRowDTO {
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @ExcelProperty("ID")
    private int id;

    @ExcelProperty("EMPLOYEE ID")
    private int empId;

    @ExcelProperty("EMPLOYEE NAME")
    private String empName;

    @ExcelProperty("EMAIL")
    private String email;

    @ExcelProperty("START")
    @DateTimeFormat("HH:mm")
    private String startTime;

    @ExcelProperty("END")
    @DateTimeFormat("HH:mm")
    private String endTime;

    @ExcelProperty("DATE")
    @DateTimeFormat("dd/MM/yyyy")
    private String date;

    @ExcelProperty("CHECKED TIME")
    @DateTimeFormat("HH:mm")
    private Date checkedTime;

    public LocalTime getStartTimeAsLocal() {
        return LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalTime getEndTimeAsLocal() {
        return LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalDate getDateAsLocal() {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

