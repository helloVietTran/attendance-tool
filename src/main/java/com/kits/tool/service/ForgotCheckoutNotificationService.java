package com.kits.tool.service;

import com.kits.tool.dto.CheckInErrorDTO;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.dto.MailSubject;
import com.kits.tool.entity.ForgotCheckoutEmailLog;
import com.kits.tool.repository.ForgotCheckoutEmailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ForgotCheckoutNotificationService {
    
    @Autowired
    private ForgotCheckoutEmailLogRepository emailLogRepository;
    
    @Autowired
    private MailService mailService;
    
    private static final int MAX_EMAIL_COUNT = 3;  // Gửi tối đa 3 lần
    private static final LocalTime SPECIAL_TIME = LocalTime.of(23, 59);
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Phát hiện và lưu log quên checkout
     */
    public void detectForgotCheckout(List<ExcelRowDTO> rawData, List<ExcelExportDTO> exportData) {
        System.out.println("🔍 Bắt đầu phát hiện quên checkout...");
        System.out.println("📊 Số bản ghi: " + exportData.size());
        
        // Tạo map để tra cứu email và tên
        Map<Integer, ExcelRowDTO> employeeInfoMap = new HashMap<>();
        for (ExcelRowDTO row : rawData) {
            employeeInfoMap.putIfAbsent(row.getEmpId(), row);
        }
        
        int forgotCheckoutCount = 0;
        
        // Duyệt qua dữ liệu đã transform
        for (ExcelExportDTO data : exportData) {
            LocalTime checkoutTime = data.getCheckoutTime() != null ? 
                LocalTime.parse(data.getCheckoutTime(), timeFmt) : null;
            
            System.out.println("👤 Nhân viên " + data.getEmpId() + " - Ngày " + data.getDate() + 
                             " - Checkout: " + (checkoutTime != null ? checkoutTime : "NULL"));
            
            // Phát hiện quên checkout (checkout = 23:59)
            // Bây giờ checkedTime là String nên không bị lệch timezone
            if (checkoutTime != null && checkoutTime.equals(SPECIAL_TIME)) {
                LocalDate incidentDate = LocalDate.parse(data.getDate(), dateFmt);
                
                // Kiểm tra xem đã có log chưa
                Optional<ForgotCheckoutEmailLog> existingLog = 
                    emailLogRepository.findByEmployeeIdAndIncidentDate(data.getEmpId(), incidentDate);
                
                if (existingLog.isEmpty()) {
                    // Tạo log mới
                    ForgotCheckoutEmailLog log = new ForgotCheckoutEmailLog();
                    log.setEmployeeId(data.getEmpId());
                    log.setIncidentDate(incidentDate);
                    log.setEmailSentCount(0);
                    emailLogRepository.save(log);
                    
                    forgotCheckoutCount++;
                    System.out.println("⚠️ Phát hiện quên checkout: Nhân viên " + data.getEmpId() + 
                                     " ngày " + incidentDate);
                } else {
                    System.out.println("ℹ️ Đã có log cho nhân viên " + data.getEmpId() + " ngày " + incidentDate);
                }
            }
        }
        
        System.out.println("✅ Tổng số trường hợp quên checkout: " + forgotCheckoutCount);
    }
    
    /**
     * Gửi email cảnh báo cho những người quên checkout
     * Gọi method này mỗi khi tool khởi động
     */
    public void sendPendingNotifications(List<ExcelRowDTO> rawData) {
        LocalDate today = LocalDate.now();
        System.out.println("📧 Bắt đầu kiểm tra email cần gửi...");
        System.out.println("📅 Ngày hôm nay: " + today);
        
        // Tạo map để tra cứu thông tin nhân viên
        Map<Integer, ExcelRowDTO> employeeInfoMap = new HashMap<>();
        for (ExcelRowDTO row : rawData) {
            employeeInfoMap.putIfAbsent(row.getEmpId(), row);
        }
        
        // Lấy danh sách chưa gửi đủ 3 lần
        List<ForgotCheckoutEmailLog> pendingLogs = 
            emailLogRepository.findByEmailSentCountLessThan(MAX_EMAIL_COUNT);
        
        System.out.println("📋 Số log chưa gửi đủ 3 lần: " + pendingLogs.size());
        
        int emailSentCount = 0;
        
        for (ForgotCheckoutEmailLog log : pendingLogs) {
            System.out.println("🔍 Kiểm tra log: Nhân viên " + log.getEmployeeId() + 
                             " - Sự cố: " + log.getIncidentDate() + 
                             " - Đã gửi: " + log.getEmailSentCount() + " lần" +
                             " - Lần cuối: " + log.getLastSentDate());
            
            // Chỉ gửi nếu:
            // 1. Chưa gửi lần nào (lastSentDate = null)
            // 2. Hoặc đã qua 1 ngày kể từ lần gửi cuối
            boolean shouldSend = log.getLastSentDate() == null || 
                                log.getLastSentDate().isBefore(today);
            
            // Chỉ gửi từ ngày hôm sau trở đi
            boolean afterIncident = today.isAfter(log.getIncidentDate());
            
            System.out.println("  ↳ shouldSend: " + shouldSend + ", afterIncident: " + afterIncident);
            
            if (shouldSend && afterIncident) {
                // Lấy thông tin nhân viên
                ExcelRowDTO employeeInfo = employeeInfoMap.get(log.getEmployeeId());
                
                if (employeeInfo != null) {
                    System.out.println("  ↳ Email nhân viên: " + employeeInfo.getEmail());
                    
                    // Gửi email
                    sendForgotCheckoutEmail(log, employeeInfo);
                    
                    // Cập nhật log
                    log.setEmailSentCount(log.getEmailSentCount() + 1);
                    log.setLastSentDate(today);
                    emailLogRepository.save(log);
                    
                    emailSentCount++;
                    System.out.println("✅ Đã gửi email lần " + log.getEmailSentCount() + 
                                     " cho nhân viên " + log.getEmployeeId() + 
                                     " (" + employeeInfo.getEmail() + ")" +
                                     " (sự cố ngày " + log.getIncidentDate() + ")");
                } else {
                    System.out.println("  ↳ ⚠️ Không tìm thấy thông tin nhân viên " + log.getEmployeeId());
                }
            } else {
                System.out.println("  ↳ ⏭️ Bỏ qua (chưa đến lúc gửi)");
            }
        }
        
        System.out.println("✅ Tổng số email đã gửi: " + emailSentCount);
    }
    
    /**
     * Gửi email cảnh báo quên checkout
     */
    private void sendForgotCheckoutEmail(ForgotCheckoutEmailLog log, ExcelRowDTO employeeInfo) {
        CheckInErrorDTO dto = new CheckInErrorDTO();
        dto.setUserId(String.valueOf(employeeInfo.getEmpId()));
        dto.setUsername(employeeInfo.getEmpName());
        dto.setEmail(employeeInfo.getEmail());
        dto.setStartWorkingShift(employeeInfo.getStartTime());
        dto.setEndWorkingShift(employeeInfo.getEndTime());
        dto.setDate(log.getIncidentDate());
        
        // Set thông tin quên checkout
        dto.setFirstCheckInTime(SPECIAL_TIME);  // Dùng 23:59 để trigger template
        dto.setLastCheckInTime(SPECIAL_TIME);   // Cả 2 giống nhau = quên checkout
        dto.setLateCheckInMinutes(0);
        dto.setEarlyCheckOutMinutes(0);
        
        // Gửi email
        mailService.sendHtmlEmail(
            dto.getEmail(),
            MailSubject.CHECK_IN_ERROR_MAIL.getSubject() + " - Quên Checkout",
            "check-in-error-mail",
            dto
        );
    }
}

