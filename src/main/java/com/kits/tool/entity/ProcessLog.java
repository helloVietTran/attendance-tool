package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="ProcessLog")
@Data
public class ProcessLog {
    @Id
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
}
