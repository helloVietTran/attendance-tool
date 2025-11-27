package com.kits.tool.service;

import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.entity.DailyWorkTimeAnalysis;
import com.kits.tool.repository.DailyWorkTimeAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WorkTimeAnalysisService {
    
    @Autowired
    private DailyWorkTimeAnalysisRepository repository;
    
    // Quy định công ty
    private static final LocalTime SHIFT_START = LocalTime.of(8, 30);  // 8:30
    private static final LocalTime SHIFT_END = LocalTime.of(17, 30);   // 17:30
    private static final int LUNCH_BREAK_MINUTES = 60;                 // 1 giờ nghỉ trưa
    private static final int REQUIRED_WORK_MINUTES = 480;              // 8 giờ = 480 phút
    
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Phân tích và lưu dữ liệu chấm công
     */
    public void analyzeAndSave(List<ExcelExportDTO> attendanceData) {
        for (ExcelExportDTO data : attendanceData) {
            // Parse dữ liệu
            LocalDate workDate = LocalDate.parse(data.getDate(), dateFmt);
            LocalTime checkinTime = data.getCheckinTime() != null ? 
                LocalTime.parse(data.getCheckinTime(), timeFmt) : null;
            LocalTime checkoutTime = data.getCheckoutTime() != null ? 
                LocalTime.parse(data.getCheckoutTime(), timeFmt) : null;
            
            // Bỏ qua nếu thiếu dữ liệu
            if (checkinTime == null || checkoutTime == null) {
                continue;
            }
            
            // Tính toán
            DailyWorkTimeAnalysis analysis = calculateWorkTime(
                data.getEmpId(), 
                workDate, 
                checkinTime, 
                checkoutTime
            );
            
            // Lưu vào DB
            repository.save(analysis);
        }
    }
    
    /**
     * Tính toán các chỉ số làm việc
     */
    private DailyWorkTimeAnalysis calculateWorkTime(
        int employeeId, 
        LocalDate workDate, 
        LocalTime checkin, 
        LocalTime checkout
    ) {
        DailyWorkTimeAnalysis analysis = new DailyWorkTimeAnalysis();
        analysis.setEmployeeId(employeeId);
        analysis.setWorkDate(workDate);
        analysis.setShiftStartTime(SHIFT_START);
        analysis.setShiftEndTime(SHIFT_END);
        
        // 1. Tính số phút đi muộn
        int lateMinutes = 0;
        if (checkin.isAfter(SHIFT_START)) {
            lateMinutes = (int) Duration.between(SHIFT_START, checkin).toMinutes();
        }
        analysis.setLateMinutes(lateMinutes);
        
        // 2. Tính số phút về sớm
        int earlyLeaveMinutes = 0;
        if (checkout.isBefore(SHIFT_END)) {
            earlyLeaveMinutes = (int) Duration.between(checkout, SHIFT_END).toMinutes();
        }
        analysis.setEarlyLeaveMinutes(earlyLeaveMinutes);
        
        // 3. Tính tổng thời gian có mặt (bao gồm cả nghỉ trưa)
        int inOfficeMinutes = (int) Duration.between(checkin, checkout).toMinutes();
        analysis.setInOfficeMinutes(inOfficeMinutes);
        
        // 4. Tính thời gian làm việc thực (trừ nghỉ trưa)
        int workTimeMinutes = inOfficeMinutes - LUNCH_BREAK_MINUTES;
        if (workTimeMinutes < 0) {
            workTimeMinutes = 0;
        }
        analysis.setWorkTimeMinutes(workTimeMinutes);
        
        // 5. Tính số phút thiếu hoặc OT
        int difference = workTimeMinutes - REQUIRED_WORK_MINUTES;
        
        if (difference < 0) {
            // Thiếu giờ
            analysis.setLackMinutes(Math.abs(difference));
            analysis.setOverTimeMinutes(0);
        } else {
            // Làm thêm giờ (OT)
            analysis.setLackMinutes(0);
            analysis.setOverTimeMinutes(difference);
        }
        
        return analysis;
    }
}

