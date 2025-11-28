package com.example.attendance.service;

import com.example.attendance.dto.AttendanceStatistics;
import com.example.attendance.entity.DailyWorkTimeAnalysis;
import com.example.attendance.repository.DailyWorkTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceStatisticsService {

    private final DailyWorkTimeRepository repository;

    /**
     * Thống kê theo ngày cụ thể
     */
    public List<AttendanceStatistics> getStatisticsByDay(LocalDate date) {
        return getStatisticsByDateRange(date, date, formatDay(date));
    }

    /**
     * Thống kê theo tháng
     */
    public List<AttendanceStatistics> getStatisticsByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        String period = String.format("%d-%02d", year, month);

        return getStatisticsByDateRange(startDate, endDate, period);
    }

    /**
     * Thống kê theo quý
     */
    public List<AttendanceStatistics> getStatisticsByQuarter(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter phải từ 1 đến 4");
        }

        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate startDate = LocalDate.of(year, startMonth, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);
        String period = String.format("Q%d-%d", quarter, year);

        return getStatisticsByDateRange(startDate, endDate, period);
    }

    /**
     * Thống kê theo năm
     */
    public List<AttendanceStatistics> getStatisticsByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        String period = String.valueOf(year);

        return getStatisticsByDateRange(startDate, endDate, period);
    }

    /**
     * Core method: Thống kê theo khoảng thời gian
     */
    private List<AttendanceStatistics> getStatisticsByDateRange(
            LocalDate startDate, LocalDate endDate, String period) {

        List<DailyWorkTimeAnalysis> data = repository.findByWorkDateBetween(startDate, endDate);

        if (data.isEmpty()) {
            log.warn("Không có dữ liệu cho khoảng thời gian: {} - {}", startDate, endDate);
            return new ArrayList<>();
        }

        // Group theo employeeId
        Map<Integer, List<DailyWorkTimeAnalysis>> groupedData = data.stream()
                .collect(Collectors.groupingBy(DailyWorkTimeAnalysis::getEmployeeId));

        // Tính toán thống kê cho từng employee
        return groupedData.entrySet().stream()
                .map(entry -> calculateStatistics(entry.getKey(), entry.getValue(), period))
                .collect(Collectors.toList());
    }

    /**
     * Tính toán thống kê cho một employee
     */
    private AttendanceStatistics calculateStatistics(
            Integer employeeId, List<DailyWorkTimeAnalysis> records, String period) {

        int totalWorkDays = records.size();

        int totalLate = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getLateMinutes)
                .sum();

        int totalEarlyLeave = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getEarlyLeaveMinutes)
                .sum();

        int totalLack = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getLackMinutes)
                .sum();

        int totalOverTime = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getOverTimeMinutes)
                .sum();

        int totalInOffice = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getInOfficeMinutes)
                .sum();

        int totalWorkTime = records.stream()
                .mapToInt(DailyWorkTimeAnalysis::getWorkTimeMinutes)
                .sum();

        // Đếm số ngày có vi phạm
        long lateDaysCount = records.stream()
                .filter(r -> r.getLateMinutes() > 0)
                .count();

        long earlyLeaveDaysCount = records.stream()
                .filter(r -> r.getEarlyLeaveMinutes() > 0)
                .count();

        long overTimeDaysCount = records.stream()
                .filter(r -> r.getOverTimeMinutes() > 0)
                .count();

        return AttendanceStatistics.builder()
                .employeeId(employeeId)
                .period(period)
                .totalWorkDays(totalWorkDays)
                .totalLateMinutes(totalLate)
                .totalEarlyLeaveMinutes(totalEarlyLeave)
                .totalLackMinutes(totalLack)
                .totalOverTimeMinutes(totalOverTime)
                .totalInOfficeMinutes(totalInOffice)
                .totalWorkTimeMinutes(totalWorkTime)
                .avgLateMinutes(totalLate * 1.0 / totalWorkDays)
                .avgEarlyLeaveMinutes(totalEarlyLeave * 1.0 / totalWorkDays)
                .avgWorkTimeMinutes(totalWorkTime * 1.0 / totalWorkDays)
                .avgOverTimeMinutes(totalOverTime * 1.0 / totalWorkDays)
                .lateDaysCount((int) lateDaysCount)
                .earlyLeaveDaysCount((int) earlyLeaveDaysCount)
                .overTimeDaysCount((int) overTimeDaysCount)
                .build();
    }

    private String formatDay(LocalDate date) {
        return date.toString();
    }
}