package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class YearMonthClosingDTO {
    @ExcelProperty("STT")
    private int stt;

    @ExcelProperty(index = 1)
    private int year;

    @ExcelProperty("1")
    private int month1;
    @ExcelProperty("2")
    private int month2;
    @ExcelProperty("3")
    private int month3;
    @ExcelProperty("4")
    private int month4;
    @ExcelProperty("5")
    private int month5;
    @ExcelProperty("6")
    private int month6;
    @ExcelProperty("7")
    private int month7;
    @ExcelProperty("8")
    private int month8;
    @ExcelProperty("9")
    private int month9;
    @ExcelProperty("10")
    private int month10;
    @ExcelProperty("11")
    private int month11;
    @ExcelProperty("12")
    private int month12;
}

