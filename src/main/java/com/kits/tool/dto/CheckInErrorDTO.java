package com.kits.tool.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CheckInErrorDTO {
    private String userId;
    private String username;
    private String email;
    private String startWorkingShift; // thời gian bắt đầu ca làm
    private String endWorkingShift; // thời gian bắt đầu ca làm
    private LocalDate date; // ngày phát sinh lỗi

    private LocalTime firstCheckInTime; // lần check in đầu tiên
    private LocalTime lastCheckInTime; // lần check in cuối cùng
    private int lateCheckInMinutes; // đi muộn
    private int earlyCheckOutMinutes; // về sớm
}