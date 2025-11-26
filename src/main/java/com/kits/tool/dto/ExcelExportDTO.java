package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import java.text.SimpleDateFormat;

public class ExcelExportDTO {
    @ExcelIgnore
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");       // chỉ giờ:phút

    @ExcelProperty("ID")
    private int checkinId;

    @ExcelProperty("ID")
    private int checkoutId;

    @ExcelProperty("EMPLOYEE ID")
    private int empId;

    @ExcelProperty("DATE")
//    @DateTimeFormat("dd/MM/yyyy")
    private String date;

    @ExcelProperty("CHECKIN TIME")
//    @DateTimeFormat("HH:mm")
    private String checkinTime;

    @ExcelProperty("CHECKOUT TIME")
//    @DateTimeFormat("HH:mm")
    private String checkoutTime;

    public int getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(int checkoutId) {
        this.checkoutId = checkoutId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}

