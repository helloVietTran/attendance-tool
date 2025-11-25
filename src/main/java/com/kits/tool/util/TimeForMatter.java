package com.kits.tool.util;

// Dùng để định dạng giờ/phút theo yêu cầu
public class TimeForMatter {

    /**
     * Dùng cho Mail Đi Muộn/Về Sớm (X phút hoặc HH:mm)
     */
    public static String formatDuration(int minutes) {
        if (minutes < 60) {
            return minutes + " phút";
        } else {
            int h = minutes / 60;
            int m = minutes % 60;
            return String.format("%02d:%02d", h, m);
        }
    }

    /**
     * Dùng cho Mail Chốt công (Tổng hợp tháng) - Luôn là HH:mm
     */
    public static String formatMonthlyHours(double decimalHours) {
        int hours = (int) decimalHours;
        int minutes = (int) Math.round((decimalHours - hours) * 60);
        return String.format("%d:%02d", hours, minutes);
    }
}