package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class DateClosingDTO {
//    @ExcelProperty("Closing Date")
    private LocalDate closingDate;
}
