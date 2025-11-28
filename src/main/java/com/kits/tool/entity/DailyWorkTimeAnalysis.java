package com.kits.tool.entity;

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
    @Column(name = "id")
    private Integer id;

    @Column(name = "employeeId", nullable = false)
    private int employeeId;

    @Column(name = "workDate", nullable = false)
    private LocalDate workDate;

    @Column(name = "lateMinutes")
    private int lateMinutes = 0;

    @Column(name = "earlyLeaveMinutes")
    private int earlyLeaveMinutes = 0;

    @Column(name = "lackMinutes")
    private int lackMinutes = 0;

    @Column(name = "overTimeMinutes")
    private int overTimeMinutes = 0;

    @Column(name = "inOfficeMinutes")
    private int inOfficeMinutes = 0;

    @Column(name = "workTimeMinutes")
    private int workTimeMinutes = 0;

    @Column(name = "shiftStartTime", nullable = false)
    private LocalTime shiftStartTime;

    @Column(name = "shiftEndTime", nullable = false)
    private LocalTime shiftEndTime;

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
