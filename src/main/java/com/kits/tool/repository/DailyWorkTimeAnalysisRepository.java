package com.kits.tool.repository;

import com.kits.tool.entity.DailyWorkTimeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyWorkTimeAnalysisRepository extends JpaRepository<DailyWorkTimeAnalysis, Integer> {
    Optional<DailyWorkTimeAnalysis> findByEmployeeIdAndWorkDate(int employeeId, LocalDate workDate);

    // Tính tổng phút làm việc thực tế (Phase 1)
    @Query("SELECT COALESCE(SUM(d.workTimeMinutes), 0) FROM DailyWorkTimeAnalysis d " +
            "WHERE d.employeeId = :empId AND d.workDate BETWEEN :startDate AND :endDate")
    Integer sumRealWorkMinutes(Integer empId, LocalDate startDate, LocalDate endDate);

    // Tính tổng phút nợ phát sinh sau ngày chốt (Phase 2 -> Đẩy sang tháng sau)
    @Query("SELECT COALESCE(SUM(d.lackMinutes), 0) FROM DailyWorkTimeAnalysis d " +
            "WHERE d.employeeId = :empId AND d.workDate BETWEEN :startDate AND :endDate")
    Integer sumLackMinutes(Integer empId, LocalDate startDate, LocalDate endDate);
}
