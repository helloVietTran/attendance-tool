package com.kits.tool.repository;

import com.kits.tool.entity.DailyWorkTimeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyWorkTimeAnalysisRepository extends JpaRepository<DailyWorkTimeAnalysis, Integer> {
    
    Optional<DailyWorkTimeAnalysis> findByEmployeeIdAndWorkDate(int employeeId, LocalDate workDate);
}

