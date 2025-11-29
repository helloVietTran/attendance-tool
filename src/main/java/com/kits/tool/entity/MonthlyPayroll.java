package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "monthly_payroll")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPayroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer employeeId;
    private Integer month;
    private Integer year;
    private Integer totalPaidMinutes;
    private Integer debtMinutesNextMonth;


}
