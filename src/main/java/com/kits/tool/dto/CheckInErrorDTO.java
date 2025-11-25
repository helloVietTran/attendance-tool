package com.kits.tool.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CheckInErrorDTO {
    private String userId;
    private String username;
    private String email;
    private String workingShift; // Giờ làm việc động (ví dụ: "08:00 ~ 17:00")
    private LocalDate errorDate;

    // Logic phân loại lỗi
    private boolean isFullCheckInMissing;
    private int lateCheckInMinutes;     // > 0 nếu đi muộn
    private int earlyCheckOutMinutes;   // > 0 nếu về sớm
}