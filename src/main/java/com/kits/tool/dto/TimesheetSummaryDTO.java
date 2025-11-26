package com.kits.tool.dto;

import lombok.Data;

@Data
public class TimesheetSummaryDTO {
    private String userId;
    private String username;
    private String email;
    private String monthYear;
    private double theoreticalDays; // Ngày công lý thuyết
    private double actualDays;      // Ngày làm thực tế
    private double leaveDays;       // Ngày nghỉ
    private double missingHours;    // Giờ thiếu
    private double totalHours;      // Tổng giờ làm
}
