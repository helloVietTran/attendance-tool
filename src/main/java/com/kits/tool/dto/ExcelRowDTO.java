package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.text.SimpleDateFormat;
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
    private String checkedTime;  // Đổi từ Date → String để tránh timezone issue
}

