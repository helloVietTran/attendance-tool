package com.kits.tool.Processor;

import com.kits.tool.Config.MailConfig;
import com.kits.tool.Mail.MailSender;
import com.kits.tool.Util.EmployeeDataImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Session;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

class ViolationRecord {
    String employeeId;
    String username;
    Date violationDate;
    int latenessMinutes;
    boolean isAbsent;
    String shiftTime = "08:30 ~ 17:30";
}

class MonthlySummaryData {
    String employeeId;
    String username;
    String shiftTime = "08:30 ~ 17:30";
    int theoreticalWorkdays;
    int actualWorkdays;
    int paidLeaveDays;
    double totalMissingHours;
    double totalWorkingHours;
}

public class TimesheetProcessor {

    private static final Logger logger = LogManager.getLogger(TimesheetProcessor.class);
    private final Session mailSession;
    private final String systemEmail = "system_timesheet@mail.com";
    private final Map<String, String> employeeEmails;

    /**
     * Constructor: Khởi tạo Session và đọc file email nhân viên.
     */
    public TimesheetProcessor(String senderUsername, String senderPassword, String employeeFilepath) {
        this.mailSession = MailConfig.createSession(senderUsername, senderPassword);
        EmployeeDataImporter importer = new EmployeeDataImporter();
        this.employeeEmails = importer.importEmployeeEmails(employeeFilepath);
        logger.info("Timesheet Processor khởi tạo thành công và tải {} địa chỉ email.", employeeEmails.size());
    }

    /**
     * Hàm chính chạy toàn bộ chu trình xử lý.
     */
    public void runProcessingCycle(Date targetDate, boolean isMonthlyEnd) {
        logger.info("=== Bắt đầu Chu trình xử lý Timesheet ngày {} ===", new SimpleDateFormat("dd/MM/yyyy").format(targetDate));

        checkAndSendDailyAlerts(targetDate);
        if (isMonthlyEnd) {
            sendMonthlySummary(targetDate);
        }

        logger.info("=== Chu trình xử lý Timesheet hoàn tất ===");
    }

    //---------------------------------------------------------
    // HÀM GỬI CẢNH BÁO ĐI TRỄ/VẮNG MẶT
    //---------------------------------------------------------
    private void checkAndSendDailyAlerts(Date targetDate) {
        logger.info("Bắt đầu kiểm tra vi phạm ngày {}", targetDate);

        List<ViolationRecord> violations = fetchDailyViolations(targetDate);

        for (ViolationRecord record : violations) {
            String employeeEmail = employeeEmails.get(record.employeeId);

            if (employeeEmail == null) {
                logger.warn("Không tìm thấy email của nhân viên ID: {}. Bỏ qua mail cảnh báo.", record.employeeId);
                continue;
            }

            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(record.violationDate);
            String violationDetail = record.isAbsent
                    ? String.format("Bạn đã quên check in/out vào ngày - %s", dateStr)
                    : String.format("Bạn đã đi trễ %d phút vào ngày - %s", record.latenessMinutes, dateStr);

            String subject = "[NO-REPLY] Thông báo lỗi timesheet";
            String body = String.format(
                    "TO: %s\nSubject: %s\n\nDear %s, ID: %s\nKhung làm việc: %s\n\nContent:\n%s",
                    employeeEmail, subject, record.username, record.employeeId, record.shiftTime, violationDetail
            );

            long mailLogId = insertMailLog(record.employeeId, employeeEmail, subject, body, "absenceAlert");
            boolean success = MailSender.sendEmail(mailSession, systemEmail, employeeEmail, subject, body);

            if (success) { updateMailLogStatus(mailLogId, "sent"); }
            else { updateMailLogStatus(mailLogId, "failed"); }
        }
    }

    //---------------------------------------------------------
    // HÀM GỬI TỔNG KẾT CUỐI THÁNG (Tính toán số ngày/giờ thực tế)
    //---------------------------------------------------------
    private void sendMonthlySummary(Date targetDate) {
        logger.info("Bắt đầu tổng kết giờ làm tháng.");

        List<MonthlySummaryData> summaries = calculateAndFetchMonthlySummaries(targetDate);

        for (MonthlySummaryData summary : summaries) {
            String employeeEmail = employeeEmails.get(summary.employeeId);

            if (employeeEmail == null) {
                logger.warn("Không tìm thấy email của nhân viên ID: {}. Bỏ qua mail tổng kết.", summary.employeeId);
                continue;
            }

            String subject = "[NO-REPLY] Mail chốt công tháng " + new SimpleDateFormat("MM/yyyy").format(targetDate);

            String body = String.format(
                    "TO: %s\nSubject: %s\n\nDear %s, ID: %s\nKhung làm việc: %s\n\n" +
                            "Ngày công trong tháng là %d ngày công ( số ngày công lý thuyết )\n" +
                            "Tổng số ngày làm việc tại công ty: %d ngày\n" +
                            "Tổng số ngày nghỉ: %d ngày\n" +
                            "Thời gian còn thiếu do đi muộn về sớm hoặc quên checkin/checkout: %.1f giờ\n" +
                            "Content:\nTổng thời gian làm việc: %.1f giờ",
                    employeeEmail, subject, summary.username, summary.employeeId, summary.shiftTime,
                    summary.theoreticalWorkdays,
                    summary.actualWorkdays,
                    summary.paidLeaveDays,
                    summary.totalMissingHours,
                    summary.totalWorkingHours
            );

            // B. Ghi Log và Gửi mail
            long mailLogId = insertMailLog(summary.employeeId, employeeEmail, subject, body, "paidNotification");
            boolean success = MailSender.sendEmail(mailSession, systemEmail, employeeEmail, subject, body);

            // C. Cập nhật trạng thái MailLog
            if (success) { updateMailLogStatus(mailLogId, "sent"); }
            else { updateMailLogStatus(mailLogId, "failed"); }
        }
        logger.info("Hoàn tất gửi {} mail tổng kết.", summaries.size());
    }

    private List<MonthlySummaryData> calculateAndFetchMonthlySummaries(Date date) {
        logger.info("Bắt đầu tính toán số ngày công và tổng giờ làm từ AttendanceLog...");
        return new ArrayList<>();
    }

    private List<ViolationRecord> fetchDailyViolations(Date date) {
        return new ArrayList<>();
    }

    private long insertMailLog(String employeeId, String recipient, String subject, String body, String type) {
        logger.debug("CHÈN bản ghi vào MailLog (ID: {}).", employeeId);
        return 1L;
    }

    private void updateMailLogStatus(long mailLogId, String status) {
        logger.debug("CẬP NHẬT MailLog ID {} sang trạng thái {}.", mailLogId, status);
    }
}