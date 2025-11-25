package com.kits.tool.dto.holidaydto;

import com.alibaba.excel.annotation.ExcelProperty;

public class HolidayDTO {
    @ExcelProperty("holidayDate")
    private String holidayDate; // dạng chuỗi, sẽ parse về LocalDate

    @ExcelProperty("detail")
    private String detail;

    @ExcelProperty("type")
    private String type;

    public String getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
