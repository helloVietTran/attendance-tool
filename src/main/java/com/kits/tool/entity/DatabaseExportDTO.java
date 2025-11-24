package com.kits.tool.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "AttendanceLog")
@IdClass(CompositeKey.class)
public class DatabaseExportDTO {
    @Id
    @Column(name = "employeeId")
    private int empId;

    @ManyToOne
    @JoinColumn(name = "processLogInId", referencedColumnName = "id",nullable = true, insertable=false, updatable=false)
    DatabaseRowDTO databaseRowDTO1;

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

    public DatabaseRowDTO getDatabaseRowDTO1() {
        return databaseRowDTO1;
    }

    public void setDatabaseRowDTO1(DatabaseRowDTO databaseRowDTO1) {
        this.databaseRowDTO1 = databaseRowDTO1;
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

//Set composite key cho cặp khóa empId và processDate
class CompositeKey implements Serializable {
    private int empId;
    private LocalDate processDate;
}
