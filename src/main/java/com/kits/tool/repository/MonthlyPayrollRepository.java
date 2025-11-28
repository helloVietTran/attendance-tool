package com.kits.tool.repository;

import com.kits.tool.entity.MonthlyPayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyPayrollRepository extends JpaRepository<MonthlyPayroll, Long> {
    Optional<MonthlyPayroll> findByEmployeeIdAndMonthAndYear(Integer empId, Integer month, Integer year);
}
