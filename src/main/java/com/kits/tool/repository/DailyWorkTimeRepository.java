package com.example.attendance.repository;

import com.example.attendance.entity.DailyWorkTimeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyWorkTimeRepository extends JpaRepository<DailyWorkTimeAnalysis, Integer> {

    // Tìm theo khoảng thời gian
    List<DailyWorkTimeAnalysis> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    // Tìm theo employee và khoảng thời gian
    List<DailyWorkTimeAnalysis> findByEmployeeIdAndWorkDateBetween(
            Integer employeeId, LocalDate startDate, LocalDate endDate);

    // Thống kê theo employee
    @Query("SELECT d FROM DailyWorkTimeAnalysis d WHERE d.employeeId = :employeeId " +
            "AND d.workDate BETWEEN :startDate AND :endDate ORDER BY d.workDate")
    List<DailyWorkTimeAnalysis> findByEmployeeAndDateRange(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Lấy danh sách employeeId unique
    @Query("SELECT DISTINCT d.employeeId FROM DailyWorkTimeAnalysis d ORDER BY d.employeeId")
    List<Integer> findDistinctEmployeeIds();
}