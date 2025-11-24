package com.kits.tool.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelRowDTO {
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");       // chỉ giờ:phút
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  // chỉ ngày/tháng/năm

    @ExcelProperty("ID")
    private int id;

    @ExcelProperty("EMPLOYEE ID")
    private int empId;

    @ExcelProperty("EMPLOYEE NAME")
    private String empName;

    @ExcelProperty("EMAIL")
    private String email;

    @ExcelProperty("START")
    @DateTimeFormat("HH:mm")
    private String startTime;

    @ExcelProperty("END")
    @DateTimeFormat("HH:mm")
    private String endTime;

    @ExcelProperty("DATE")
    @DateTimeFormat("dd/MM/yyyy")
    private String date;

    @ExcelProperty("CHECKED TIME")
    @DateTimeFormat("HH:mm")
    private Date checkedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(Date checkedTime) {
        this.checkedTime = checkedTime;
    }

//    @Override
//    public String toString() {
//        return "Employee{" +
//                "id=" + id +
//                ", empName='" + empName + '\'' +
//                ", email='" + email + '\'' +
//                ", startTime='" + (startTime != null ? timeFormat.format(startTime) : null) + '\'' +
//                ", endTime='" + (endTime != null ? timeFormat.format(endTime) : null) + '\'' +
//                ", date='" + (date != null ? dateFormat.format(date) : null) + '\'' +
//                ", checkedTime='" + (checkedTime != null ? timeFormat.format(checkedTime) : null) + '\'' +
//                '}';
//    }
}

