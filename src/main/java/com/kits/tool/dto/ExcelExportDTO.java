package com.kits.tool.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ExcelExportDTO {
//    @ExcelIgnore
//    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");       // chỉ giờ:phút

//    @ExcelProperty("ID")
    private int checkinId;

//    @ExcelProperty("ID")
    private int checkoutId;

//    @ExcelProperty("EMPLOYEE ID")
    private int empId;

//    @ExcelProperty("DATE")
//    @DateTimeFormat("dd/MM/yyyy")
    private LocalDate date;

//    @ExcelProperty(value = "CHECKIN TIME", converter = LocalTimeConverter.class)
//    @DateTimeFormat("HH:mm")
    private LocalTime checkinTime;

//    @ExcelProperty(value = "CHECKOUT TIME", converter = LocalTimeConverter.class)
//    @DateTimeFormat("HH:mm")
    private LocalTime checkoutTime;
    }


