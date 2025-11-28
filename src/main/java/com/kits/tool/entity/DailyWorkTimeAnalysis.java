package com.example.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "DailyWorkTimeAnalysis")
@Data
public class DailyWorkTimeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer employeeId;

    @Column(nullable = false)
    private LocalDate workDate;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer lateMinutes = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer earlyLeaveMinutes = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer lackMinutes = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer overTimeMinutes = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer inOfficeMinutes = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer workTimeMinutes = 0;

    @Column(nullable = false)
    private LocalTime shiftStartTime;

    @Column(nullable = false)
    private LocalTime shiftEndTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

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