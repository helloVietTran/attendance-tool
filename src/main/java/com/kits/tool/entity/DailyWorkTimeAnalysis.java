package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "DailyWorkTimeAnalysis",
       indexes = {
           @Index(name = "idx_employee_workdate", columnList = "employeeId, workDate"),
           @Index(name = "idx_workdate", columnList = "workDate")
       })
@Data
public class DailyWorkTimeAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer employeeId;

    @Column(nullable = false)
    private LocalDate workDate;

    private Integer lateMinutes = 0;
    private Integer earlyLeaveMinutes = 0;
    private Integer lackMinutes = 0;
    private Integer overTimeMinutes = 0;
    private Integer inOfficeMinutes = 0;
    private Integer workTimeMinutes = 0;

    @Column(nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalTime shiftStartTime;

    @Column(nullable = false)
    private LocalTime shiftEndTime;

    public DailyWorkTimeAnalysis(Integer id, Integer employeeId, LocalDate workDate, Integer lateMinutes, Integer earlyLeaveMinutes, Integer lackMinutes, Integer overTimeMinutes, Integer inOfficeMinutes, Integer workTimeMinutes, LocalDateTime createdAt, LocalDateTime updatedAt, LocalTime shiftStartTime, LocalTime shiftEndTime) {
        this.id = id;
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.lateMinutes = lateMinutes;
        this.earlyLeaveMinutes = earlyLeaveMinutes;
        this.lackMinutes = lackMinutes;
        this.overTimeMinutes = overTimeMinutes;
        this.inOfficeMinutes = inOfficeMinutes;
        this.workTimeMinutes = workTimeMinutes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
    }
    public DailyWorkTimeAnalysis() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public Integer getLackMinutes() {
        return lackMinutes;
    }

    public void setLackMinutes(Integer lackMinutes) {
        this.lackMinutes = lackMinutes;
    }

    public Integer getOverTimeMinutes() {
        return overTimeMinutes;
    }

    public void setOverTimeMinutes(Integer overTimeMinutes) {
        this.overTimeMinutes = overTimeMinutes;
    }

    public Integer getInOfficeMinutes() {
        return inOfficeMinutes;
    }

    public void setInOfficeMinutes(Integer inOfficeMinutes) {
        this.inOfficeMinutes = inOfficeMinutes;
    }

    public Integer getWorkTimeMinutes() {
        return workTimeMinutes;
    }

    public void setWorkTimeMinutes(Integer workTimeMinutes) {
        this.workTimeMinutes = workTimeMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalTime getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(LocalTime shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public LocalTime getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(LocalTime shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }
}
