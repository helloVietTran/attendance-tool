package com.kits.tool.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column
    private String department;

    public Employee() {
    }

    public Employee(String name, String email, LocalDate hireDate, String department) {
        this.name = name;
        this.email = email;
        this.hireDate = hireDate;
        this.department = department;
    }
}