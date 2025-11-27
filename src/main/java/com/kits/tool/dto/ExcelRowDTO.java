package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
//    @DateTimeFormat("HH:mm")
    private Date startTime;

    @ExcelProperty("END")
//    @DateTimeFormat("HH:mm")
    private Date endTime;

    @ExcelProperty("DATE")
//    @DateTimeFormat("dd/MM/yyyy")
    private Date date;

    @ExcelProperty("CHECKED TIME")
//    @DateTimeFormat("HH:mm")
    private Date checkedTime;
}

