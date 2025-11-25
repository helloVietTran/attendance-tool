package com.kits.tool.controller;

import com.kits.tool.service.MailService;
import com.kits.tool.util.TimeForMatter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // Email nhận thư sẽ được truyền vào qua tham số 'to'
    private static final String RECEIVER_EMAIL = "nguyenlan2000ts@gmail.com";

    // --- 1. Test Mail Báo Đi Muộn (Ví dụ: 90 phút trễ) ---
    @GetMapping("/resources/templates/late-arrival")
    public String testLateArrival(@RequestParam(defaultValue = RECEIVER_EMAIL) String to) {

        int lateMinutes = 90; // 1 giờ 30 phút
        String formattedTime = TimeForMatter.formatDuration(lateMinutes);

        Map<String, Object> props = new HashMap<>();
        props.put("username", "TEST USER");
        props.put("userId", "T001");
        props.put("workingShift", "08:00 ~ 17:00 (Hành chính)");
        props.put("date", LocalDate.now().toString());
        props.put("lateDuration", formattedTime); // "01:30"

        mailService.sendHtmlEmail(
                to,
                "[TEST] Thông báo Lỗi Đi Muộn: " + formattedTime,
                "late-arrival",
                props
        );
        return "Đã kích hoạt gửi mail Đi Muộn tới " + to + ". Vui lòng kiểm tra hộp thư!";
    }

    // --- 2. Test Mail Chốt Công (Ví dụ: 160.75 giờ làm) ---
    @GetMapping("/resources/templates/monthly-summary")
    public String testMonthlySummary(@RequestParam(defaultValue = RECEIVER_EMAIL) String to) {

        double totalHours = 160.75; // 160 giờ 45 phút
        double missingHours = 5.5;  // 5 giờ 30 phút

        Map<String, Object> props = new HashMap<>();
        props.put("username", "TEST USER");
        props.put("userId", "T001");
        props.put("workingShift", "Linh hoạt 9:00 - 18:00");
        props.put("monthYear", "11/2025");
        props.put("theoreticalDays", 22.0);
        props.put("actualDays", 20.5);
        props.put("leaveDays", 1.5);

        // Sử dụng TimeFormatter để định dạng HH:mm
        props.put("missingHoursStr", TimeForMatter.formatMonthlyHours(missingHours)); // "5:30"
        props.put("totalHoursStr", TimeForMatter.formatMonthlyHours(totalHours)); // "160:45"

        mailService.sendHtmlEmail(
                to,
                "[TEST] Mail Chốt Công Tháng 11",
                "monthly-summary",
                props
        );
        return "Đã kích hoạt gửi mail Chốt Công tới " + to + ". Vui lòng kiểm tra hộp thư!";
    }
}