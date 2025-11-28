package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.kits.tool.service.excel.LocalTimeConverter;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExcelRowDTO {

    @ExcelProperty("ID")
    private int id;

    @ExcelProperty("EMPLOYEE ID")
    private int empId;

    @ExcelProperty("EMPLOYEE NAME")
    private String empName;

    @ExcelProperty("EMAIL")
    private String email;

    @ExcelProperty(value = "START", converter = LocalTimeConverter.class)
    private LocalTime startTime;

    @ExcelProperty(value = "END", converter = LocalTimeConverter.class)
    private LocalTime endTime;

    @ExcelProperty("DATE")
    @DateTimeFormat("dd/MM/yyyy")
    private LocalDate date;

    @ExcelProperty(value = "CHECKED TIME", converter = LocalTimeConverter.class)
    private LocalTime checkedTime;
}

