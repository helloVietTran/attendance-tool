package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ForgotCheckoutEmailLog")
@Data
public class ForgotCheckoutEmailLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "employeeId", nullable = false)
    private int employeeId;
    
    @Column(name = "incidentDate", nullable = false)
    private LocalDate incidentDate;  // Ngày xảy ra sự cố quên checkout
    
    @Column(name = "emailSentCount")
    private int emailSentCount = 0;  // Số lần đã gửi email
    
    @Column(name = "lastSentDate")
    private LocalDate lastSentDate;  // Ngày gửi email lần cuối
    
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

