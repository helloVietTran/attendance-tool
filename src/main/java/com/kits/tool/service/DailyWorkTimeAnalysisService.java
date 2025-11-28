package com.kits.tool.service;

import com.kits.tool.entity.AttendanceLog;
import com.kits.tool.entity.DailyWorkTimeAnalysis;
import com.kits.tool.entity.ProcessLog;
import com.kits.tool.repository.AttendanceLogRepository;
import com.kits.tool.repository.DailyWorkTimeAnalysisRepository;
import com.kits.tool.repository.ProcessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class DailyWorkTimeAnalysisService {

    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    @Autowired
    private ProcessLogRepository processLogRepository;

    @Autowired
    private DailyWorkTimeAnalysisRepository dailyWorkTimeAnalysisRepository;

    private static final LocalTime LUNCH_START = LocalTime.of(12, 0, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(13, 0, 0);
    private static final int LUNCH_BREAK_MINUTES = 60;
    private static final int STANDARD_WORK_MINUTES = 8 * 60; // 8 giờ = 480 phút

    /**
     * Xử lý và lưu phân tích thời gian làm việc hàng ngày cho tất cả nhân viên
     */
    public void processAllDailyWorkTime() {
        List<AttendanceLog> attendanceLogs = attendanceLogRepository.findAll();

        for (AttendanceLog log : attendanceLogs) {
            processDailyWorkTime(log);
        }
    }

    /**
     * Xử lý phân tích thời gian làm việc cho 1 bản ghi chấm công
     */
    public void processDailyWorkTime(AttendanceLog attendanceLog) {
        if (attendanceLog.getCheckinTime() == null || attendanceLog.getCheckoutTime() == null) {
            return; // Bỏ qua nếu thiếu checkin hoặc checkout
        }

        // Lấy thông tin ca làm việc từ ProcessLog
        ProcessLog processLogIn = processLogRepository.findById(attendanceLog.getProcessLogInId()).orElse(null);
        if (processLogIn == null) {
            return;
        }

        LocalTime shiftStartTime = processLogIn.getStartTime();
        LocalTime shiftEndTime = processLogIn.getEndTime();
        LocalTime checkinTime = attendanceLog.getCheckinTime();
        LocalTime checkoutTime = attendanceLog.getCheckoutTime();

        // Tính toán các chỉ số
        int lateMinutes = calculateLateMinutes(checkinTime, shiftStartTime);
        int earlyLeaveMinutes = calculateEarlyLeaveMinutes(checkoutTime, shiftEndTime);
        int inOfficeMinutes = calculateInOfficeMinutes(checkinTime, checkoutTime);
        int workTimeMinutes = calculateWorkTimeMinutes(checkinTime, checkoutTime, inOfficeMinutes);
        int lackMinutes = calculateLackMinutes(workTimeMinutes);

        // Tạo hoặc cập nhật bản ghi
        DailyWorkTimeAnalysis analysis = dailyWorkTimeAnalysisRepository
                .findByEmployeeIdAndWorkDate(attendanceLog.getEmpId(), attendanceLog.getProcessDate())
                .orElse(new DailyWorkTimeAnalysis());

        analysis.setEmployeeId(attendanceLog.getEmpId());
        analysis.setWorkDate(attendanceLog.getProcessDate());
        analysis.setShiftStartTime(shiftStartTime);
        analysis.setShiftEndTime(shiftEndTime);
        analysis.setLateMinutes(lateMinutes);
        analysis.setEarlyLeaveMinutes(earlyLeaveMinutes);
        analysis.setInOfficeMinutes(inOfficeMinutes);
        analysis.setWorkTimeMinutes(workTimeMinutes);
        analysis.setLackMinutes(lackMinutes);
        analysis.setOverTimeMinutes(0);

        dailyWorkTimeAnalysisRepository.save(analysis);
    }

    /**
     * Tính số phút đi muộn
     */
    private int calculateLateMinutes(LocalTime checkinTime, LocalTime shiftStartTime) {
        if (checkinTime.isAfter(shiftStartTime)) {
            return (int) Duration.between(shiftStartTime, checkinTime).toMinutes();
        }
        return 0;
    }

    /**
     * Tính số phút về sớm
     */
    private int calculateEarlyLeaveMinutes(LocalTime checkoutTime, LocalTime shiftEndTime) {
        if (checkoutTime.isBefore(shiftEndTime)) {
            return (int) Duration.between(checkoutTime, shiftEndTime).toMinutes();
        }
        return 0;
    }

    /**
     * Tính thời gian ở văn phòng (checkout - checkin)
     */
    private int calculateInOfficeMinutes(LocalTime checkinTime, LocalTime checkoutTime) {
        return (int) Duration.between(checkinTime, checkoutTime).toMinutes();
    }

    /**
     * Tính thời gian làm việc thực tế
     * Trừ đi 1 giờ nghỉ trưa nếu khoảng checkin-checkout bao gồm 12:00-13:00
     */
    private int calculateWorkTimeMinutes(LocalTime checkinTime, LocalTime checkoutTime, int inOfficeMinutes) {
        // Kiểm tra xem có nghỉ trưa không
        boolean hasLunchBreak = isLunchBreakIncluded(checkinTime, checkoutTime);
        
        if (hasLunchBreak) {
            return inOfficeMinutes - LUNCH_BREAK_MINUTES;
        }
        
        return inOfficeMinutes;
    }

    /**
     * Kiểm tra xem khoảng thời gian checkin-checkout có bao gồm giờ nghỉ trưa 12:00-13:00 không
     */
    private boolean isLunchBreakIncluded(LocalTime checkinTime, LocalTime checkoutTime) {
        // Nghỉ trưa được tính khi:
        // - checkin trước hoặc bằng 12:00
        // - checkout sau hoặc bằng 13:00
        return (checkinTime.isBefore(LUNCH_END) || checkinTime.equals(LUNCH_START)) 
            && (checkoutTime.isAfter(LUNCH_START) || checkoutTime.equals(LUNCH_END));
    }

    /**
     * Tính số phút thiếu giờ (so với 8 giờ chuẩn)
     */
    private int calculateLackMinutes(int workTimeMinutes) {
        int lack = STANDARD_WORK_MINUTES - workTimeMinutes;
        return lack > 0 ? lack : 0;
    }
}
