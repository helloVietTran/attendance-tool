package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DateClosingDTO {
    @ExcelProperty("Closing Date")
    private String closingDate;
}
