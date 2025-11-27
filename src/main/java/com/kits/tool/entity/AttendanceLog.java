package com.kits.tool.entity;

import com.kits.tool.entity.compkey.AttendanceLogCompositeKey;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "AttendanceLog")
@IdClass(AttendanceLogCompositeKey.class)
@Data
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

}