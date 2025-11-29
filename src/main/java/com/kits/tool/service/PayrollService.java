package com.kits.tool.service;

import com.kits.tool.entity.CutoffSchedule;
import com.kits.tool.entity.Employee;
import com.kits.tool.entity.MonthlyPayroll;
import com.kits.tool.repository.CutoffScheduleRepository;
import com.kits.tool.repository.DailyWorkTimeAnalysisRepository;
import com.kits.tool.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepo;

    // Quy ước: 1 ngày công chuẩn = 480 phút (8 tiếng)
    private static final int STANDARD_MINUTES_PER_DAY = 480;

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

    private void calculateMonthlySalary(int month, int year, Integer employeeId) {
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

        //Truy vấn nợ tháng trước
        int prevMonth = (month == 1) ? 12 : month - 1;
        int prevYear = (month == 1) ? year - 1 : year;

        System.out.println(">> Đang tìm dữ liệu nợ tháng: " + prevMonth + "/" + prevYear);

        MonthlyPayroll prevPayroll = payrollRepo.findByEmployeeIdAndMonthAndYear(employeeId, prevMonth, prevYear)
                .orElse(null);
        int debtFromLastMonth = 0;
        if (prevPayroll == null) {
            //Nếu không tìm thấy tháng trước, coi như không nợ
            System.out.println("Không tìm thấy bảng lương tháng " + prevMonth + ". Mặc định nợ = 0.");
        } else {
            // Có bản ghi, check null cho field debtMinutesNextMonth
            debtFromLastMonth = (prevPayroll.getDebtMinutesNextMonth() == null) ? 0 : prevPayroll.getDebtMinutesNextMonth();
            System.out.println("Tìm thấy bảng lương tháng trước. Số phút nợ: " + debtFromLastMonth);
        }

        //TỔNG PHÚT ĐƯỢC TRẢ LƯƠNG
        int totalPaidMinutes = realWorkMinutes + assumedWorkMinutes - debtFromLastMonth;

        //Tính nợ cho tháng sau
        Integer lackMinutesAfterCutoff = dailyRepo.sumLackMinutes(employeeId, cutoffDate.plusDays(1), endOfMonth);
        if (lackMinutesAfterCutoff == null) lackMinutesAfterCutoff = 0;

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

        System.out.println(">> KẾT QUẢ: Tổng phút lương = " + totalPaidMinutes
                + " (Thực tế: " + realWorkMinutes
                + " + Giả định: " + assumedWorkMinutes
                + " - Trừ nợ cũ: " + debtFromLastMonth + ")");
        System.out.println(">> Nợ đẩy sang tháng sau: " + lackMinutesAfterCutoff);
        System.out.println("------------------------------------------------");
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

    public void calculateMonthlySalaryForAllEmployee() {
        List<Employee> employees = employeeRepo.findAll();
        if (employees.isEmpty()) {
            System.out.println("Không có bản ghi nào trong bảng Employee.");
        } else {
            LocalDate today = LocalDate.now();

            CutoffSchedule schedule = cutoffRepo.findByMonthAndYear(today.getMonthValue(), today.getYear())
                    .orElse(null);

            if (schedule == null) {
                System.out.println("Chưa có lịch chốt công cho tháng " + today.getMonthValue() + "/" + today.getYear() + ". Dừng tính lương.");
                return;
            }

            LocalDate cutoffDate = schedule.getCutoffDate();

            if (!today.equals(cutoffDate)) {
                System.out.println("Hôm nay (" + today + ") chưa đến ngày chốt công (" + cutoffDate + "). Dừng tính lương.");
                return;
            }

            for (Employee e : employees) {
                this.calculateMonthlySalary(today.getMonthValue(), today.getYear(), e.getId());
            }


        }
    }
}
