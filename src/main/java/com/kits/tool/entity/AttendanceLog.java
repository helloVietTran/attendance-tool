package com.kits.tool.entity;

import com.kits.tool.entity.compkey.AttendanceLogCompositeKey;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "AttendanceLog")
@IdClass(AttendanceLogCompositeKey.class)
public class AttendanceLog {
    @Id
    @Column(name = "employeeId")
    private int empId;

    @ManyToOne
    @JoinColumn(name = "processLogInId", referencedColumnName = "id",nullable = true, insertable=false, updatable=false)
    ProcessLog processLog1;

    @Column(name = "processLogInId")
    private Integer processLogInId;

    @Column(name = "processLogOutId")
    private Integer processLogOutId;

    @Id
    @Column(name = "processDate")
    private LocalDate processDate;

    @Column(name = "checkinTime")
    private LocalTime checkinTime;

    @Column(name = "checkoutTime")
    private LocalTime checkoutTime;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getProcessLogInId() {
        return processLogInId;
    }

    public void setProcessLogInId(int processLogInId) {
        this.processLogInId = processLogInId;
    }

    public int getProcessLogOutId() {
        return processLogOutId;
    }

    public void setProcessLogOutId(int processLogOutId) {
        this.processLogOutId = processLogOutId;
    }

    public LocalDate getProcessDate() {
        return processDate;
    }

    public void setProcessDate(LocalDate processDate) {
        this.processDate = processDate;
    }

    public ProcessLog getDatabaseRowDTO1() {
        return processLog1;
    }

    public void setDatabaseRowDTO1(ProcessLog processLog1) {
        this.processLog1 = processLog1;
    }

    public LocalTime getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(LocalTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public LocalTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}