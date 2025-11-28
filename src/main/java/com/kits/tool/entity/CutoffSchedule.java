package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cutoff_schedule")
@Data
@NoArgsConstructor
public class CutoffSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer month;
    private Integer year;
    private LocalDate cutoffDate;

    public CutoffSchedule(Integer month, Integer year, LocalDate cutoffDate) {
        this.month = month;
        this.year = year;
        this.cutoffDate = cutoffDate;
    }
}
