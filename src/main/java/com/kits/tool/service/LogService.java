package com.kits.tool.service;

import com.kits.tool.entity.AppLog;
import com.kits.tool.repository.AppLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final AppLogRepository appLogRepository;

    /**
     * Hàm ghi log.
     * @param type: Loại hành động (VD: "MAIL_SENT", "LOGIN")
     * @param target: Đối tượng (VD: "nguyenvana@gmail.com")
     * @param desc: Mô tả (VD: "Tiêu đề mail: Cảnh báo")
     * @param status: Trạng thái ("SUCCESS"/"FAILED")
     * @param error: Chi tiết lỗi (nếu có)
     */
    @Async // Chạy ngầm, không block luồng chính
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Luôn tạo transaction mới để log vẫn lưu dù transaction chính bị rollback
    public void saveLog(String type, String target, String desc, String status, String error) {
        try {
            AppLog log = AppLog.builder()
                    .actionType(type)
                    .target(target)
                    .description(desc)
                    .status(status)
                    .errorMessage(error)
                    .build();

            appLogRepository.save(log);

        } catch (Exception e) {
            // Nếu ghi log thất bại thì chỉ in ra console, không làm chết app
            System.err.println("Không thể ghi log vào DB: " + e.getMessage());
        }
    }
}