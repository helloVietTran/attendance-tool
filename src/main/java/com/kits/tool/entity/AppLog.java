package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_action_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Loại hành động: "SEND_MAIL", "USER_LOGIN", "CRON_JOB", "EXPORT_REPORT"
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    // Đối tượng tác động: Email người nhận, User ID đăng nhập, Tên file...
    @Column(name = "target", length = 255)
    private String target;

    // Mô tả chi tiết: Tiêu đề mail, IP người dùng, Kết quả job...
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Trạng thái: "SUCCESS", "FAILED", "WARNING"
    @Column(name = "status", length = 20)
    private String status;

    // Chi tiết lỗi (nếu có)
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // Thời gian ghi log (Tự động)
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}