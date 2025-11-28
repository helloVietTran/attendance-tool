package com.kits.tool.service;

import com.kits.tool.entity.CutoffSchedule;
import com.kits.tool.entity.MonthlyPayroll;
import com.kits.tool.repository.CutoffScheduleRepository;
import com.kits.tool.repository.DailyWorkTimeAnalysisRepository;
import com.kits.tool.repository.MonthlyPayrollRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final DailyWorkTimeAnalysisRepository dailyRepo;
    private final MonthlyPayrollRepository payrollRepo;
    private final CutoffScheduleRepository cutoffRepo;

    // Quy ước: 1 ngày công chuẩn = 480 phút (8 tiếng)
    private static final int STANDARD_MINUTES_PER_DAY = 480;

    // B1: Sinh lịch chốt công cả năm (Chạy 1 lần/năm)
    @Transactional
    public void generateCutoffSchedule(int year) {
        List<CutoffSchedule> schedules = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int day = switch (month) {
                case 1, 2 -> 25;
                case 4, 7 -> 21;
                case 9, 12 -> 22;
                default -> 20; // 3, 5, 6, 8, 10, 11
            };

            LocalDate date = LocalDate.of(year, month, day);
            // Tranh ngay T7, CN (optional)
//            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) date = date.minusDays(2);
//            else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) date = date.minusDays(1);


            CutoffSchedule schedule = cutoffRepo.findByMonthAndYear(month, year).orElse(new CutoffSchedule());
            schedule.setMonth(month);
            schedule.setYear(year);
            schedule.setCutoffDate(date);
            schedules.add(schedule);
        }
        cutoffRepo.saveAll(schedules);
        System.out.println("Đã sinh lịch chốt công năm " + year);
    }
    // B2: Tính công tháng cho nhân viên
    @Transactional
    public void calculateMonthlySalary(int month, int year, Integer employeeId) {
        // Lấy ngày chốt công
        CutoffSchedule schedule = cutoffRepo.findByMonthAndYear(month, year)
                .orElseThrow(() -> new RuntimeException("Chưa có lịch chốt công tháng " + month));
        LocalDate cutoffDate = schedule.getCutoffDate();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        //Công thực tế (Đầu tháng -> Ngày chốt)
        Integer realWorkMinutes = dailyRepo.sumRealWorkMinutes(employeeId, startOfMonth, cutoffDate);

        //Công giả định (Sau chốt -> Cuối tháng)
        // Mặc định tính đủ công (trừ T7, CN)
        int standardDays = countBusinessDays(cutoffDate.plusDays(1), endOfMonth);
        int assumedWorkMinutes = standardDays * STANDARD_MINUTES_PER_DAY;

        //Trừ nợ tháng trước
        int prevMonth = (month == 1) ? 12 : month - 1;
        int prevYear = (month == 1) ? year - 1 : year;

        Integer debtFromLastMonth = payrollRepo.findByEmployeeIdAndMonthAndYear(employeeId, prevMonth, prevYear)
                .map(MonthlyPayroll::getDebtMinutesNextMonth) // Lấy số phút nợ
                .orElse(0);

        //TỔNG PHÚT ĐƯỢC TRẢ LƯƠNG
        int totalPaidMinutes = realWorkMinutes + assumedWorkMinutes - debtFromLastMonth;

        //Tính nợ cho tháng sau
        Integer lackMinutesAfterCutoff = dailyRepo.sumLackMinutes(employeeId, cutoffDate.plusDays(1), endOfMonth);

        //LƯU KẾT QUẢ
        MonthlyPayroll payroll = payrollRepo.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElse(MonthlyPayroll.builder()
                        .employeeId(employeeId)
                        .month(month)
                        .year(year)
                        .build());

        payroll.setTotalPaidMinutes(totalPaidMinutes);
        payroll.setDebtMinutesNextMonth(lackMinutesAfterCutoff);

        payrollRepo.save(payroll);

        System.out.println("Đã tính xong lương tháng " + month + ". Công hưởng: " + (totalPaidMinutes/480.0) + " công. Nợ tháng sau: " + lackMinutesAfterCutoff + " phút.");
    }

    // Hàm đếm số ngày làm việc (trừ T7, CN)
    private int countBusinessDays(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) return 0;
        int count = 0;
        LocalDate date = start;
        while (!date.isAfter(end)) {
            DayOfWeek d = date.getDayOfWeek();
            if (d != DayOfWeek.SATURDAY && d != DayOfWeek.SUNDAY) {
                count++;
            }
            date = date.plusDays(1);
        }
        return count;
    }
}
