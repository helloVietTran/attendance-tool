package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class HolidayDTO {
    @ExcelProperty("holidayDate")
    private String holidayDate; // parse chuỗi thời gian về LocalDate

    @ExcelProperty("detail")
    private String detail;

    @ExcelProperty("type")
    private String type;
}
