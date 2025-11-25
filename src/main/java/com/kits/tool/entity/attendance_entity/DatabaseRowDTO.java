package com.kits.tool.entity.attendance_entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="ProcessLog")
public class DatabaseRowDTO {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "employeeId")
    private int empId;

    @Column(name = "startTime")
    private LocalTime startTime;

    @Column(name = "endTime")
    private LocalTime endTime;

    @Column(name = "processDate", nullable = false)
    private LocalDate date;

    @Column(name = "checkedTime")
    private LocalTime checkedTime;

    public void setId(int id) {
        this.id = id;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCheckedTime(LocalTime checkedTime) {
        this.checkedTime = checkedTime;
    }

    public int getId() {
        return id;
    }

    public int getEmpId() {
        return empId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getCheckedTime() {
        return checkedTime;
    }
}
