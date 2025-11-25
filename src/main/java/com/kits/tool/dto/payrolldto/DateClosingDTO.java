package com.kits.tool.dto.payrolldto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.time.LocalDate;

public class DateClosingDTO {
    @ExcelProperty("Closing Date")
    private String closingDate;

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }
}
