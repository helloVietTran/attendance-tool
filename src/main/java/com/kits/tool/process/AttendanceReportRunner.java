package com.example.attendance.runner;

import com.example.attendance.dto.AttendanceStatistics;
import com.example.attendance.service.AttendanceStatisticsService;
import com.example.attendance.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * CommandLineRunner để chạy tool theo menu
 * Chỉ chạy khi user chọn, không tự động sinh file
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceReportRunner implements CommandLineRunner {

    private final AttendanceStatisticsService statisticsService;
    private final ExcelExportService excelExportService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        log.info("\n========================================");
        log.info("    TOOL THỐNG KÊ CHẤM CÔNG");
        log.info("========================================\n");

        while (running) {
            displayMenu();

            try {
                System.out.print("Chọn chức năng (0-5): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 0:
                        running = false;
                        log.info("Thoát chương trình. Tạm biệt!");
                        break;
                    case 1:
                        handleDailyReport(scanner);
                        break;
                    case 2:
                        handleMonthlyReport(scanner);
                        break;
                    case 3:
                        handleQuarterlyReport(scanner);
                        break;
                    case 4:
                        handleYearlyReport(scanner);
                        break;
                    case 5:
                        handleCustomReport(scanner);
                        break;
                    default:
                        log.warn("Lựa chọn không hợp lệ!");
                }

                if (running) {
                    System.out.println("\nNhấn Enter để tiếp tục...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                log.error("Lỗi: {}", e.getMessage());
                scanner.nextLine(); // Clear buffer
            }
        }

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n========================================");
        System.out.println("MENU CHỨC NĂNG:");
        System.out.println("========================================");
        System.out.println("1. Thống kê theo NGÀY");
        System.out.println("2. Thống kê theo THÁNG");
        System.out.println("3. Thống kê theo QUÝ");
        System.out.println("4. Thống kê theo NĂM");
        System.out.println("5. Thống kê tùy chỉnh (xem console)");
        System.out.println("0. Thoát");
        System.out.println("========================================");
    }

    private void handleDailyReport(Scanner scanner) {
        System.out.print("Nhập ngày (yyyy-MM-dd, hoặc Enter để dùng hôm nay): ");
        String dateStr = scanner.nextLine().trim();

        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        List<AttendanceStatistics> stats = statisticsService.getStatisticsByDay(date);
        displayStatistics(stats);

        if (!stats.isEmpty() && confirmExport(scanner)) {
            String filePath = excelExportService.exportToExcel(stats, "NGAY");
            if (filePath != null) {
                log.info("✓ File đã được lưu tại: {}", filePath);
            }
        }
    }

    private void handleMonthlyReport(Scanner scanner) {
        System.out.print("Nhập năm (hoặc Enter để dùng năm hiện tại): ");
        String yearStr = scanner.nextLine().trim();
        int year = yearStr.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(yearStr);

        System.out.print("Nhập tháng (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        List<AttendanceStatistics> stats = statisticsService.getStatisticsByMonth(year, month);
        displayStatistics(stats);

        if (!stats.isEmpty() && confirmExport(scanner)) {
            String filePath = excelExportService.exportToExcel(stats, "THANG");
            if (filePath != null) {
                log.info("✓ File đã được lưu tại: {}", filePath);
            }
        }
    }

    private void handleQuarterlyReport(Scanner scanner) {
        System.out.print("Nhập năm (hoặc Enter để dùng năm hiện tại): ");
        String yearStr = scanner.nextLine().trim();
        int year = yearStr.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(yearStr);

        System.out.print("Nhập quý (1-4): ");
        int quarter = scanner.nextInt();
        scanner.nextLine();

        List<AttendanceStatistics> stats = statisticsService.getStatisticsByQuarter(year, quarter);
        displayStatistics(stats);

        if (!stats.isEmpty() && confirmExport(scanner)) {
            String filePath = excelExportService.exportToExcel(stats, "QUY");
            if (filePath != null) {
                log.info("✓ File đã được lưu tại: {}", filePath);
            }
        }
    }

    private void handleYearlyReport(Scanner scanner) {
        System.out.print("Nhập năm (hoặc Enter để dùng năm hiện tại): ");
        String yearStr = scanner.nextLine().trim();
        int year = yearStr.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(yearStr);

        List<AttendanceStatistics> stats = statisticsService.getStatisticsByYear(year);
        displayStatistics(stats);

        if (!stats.isEmpty() && confirmExport(scanner)) {
            String filePath = excelExportService.exportToExcel(stats, "NAM");
            if (filePath != null) {
                log.info("✓ File đã được lưu tại: {}", filePath);
            }
        }
    }

    private void handleCustomReport(Scanner scanner) {
        log.info("Chức năng này chỉ hiển thị dữ liệu trên console, không xuất Excel.");
        log.info("Dùng để kiểm tra nhanh dữ liệu.");

        System.out.print("Nhập ngày bắt đầu (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Nhập ngày kết thúc (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());

        // Gọi trực tiếp repository hoặc tạo method mới trong service
        log.info("Hiển thị dữ liệu từ {} đến {}", startDate, endDate);
        log.info("(Chức năng này có thể mở rộng thêm)");
    }

    private void displayStatistics(List<AttendanceStatistics> stats) {
        if (stats.isEmpty()) {
            log.warn("⚠ Không có dữ liệu!");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("KẾT QUẢ THỐNG KÊ:");
        System.out.println("========================================");

        for (AttendanceStatistics stat : stats) {
            System.out.printf("\n--- Nhân viên %d - Kỳ: %s ---\n",
                    stat.getEmployeeId(), stat.getPeriod());
            System.out.printf("  Số ngày làm việc: %d ngày\n", stat.getTotalWorkDays());
            System.out.printf("  Tổng giờ làm việc: %.2f giờ\n", stat.getTotalWorkHours());
            System.out.printf("  Tổng giờ OT: %.2f giờ\n", stat.getTotalOverTimeHours());
            System.out.printf("  Trung bình làm việc: %.2f giờ/ngày\n", stat.getAvgWorkHours());
            System.out.printf("  Số ngày đi muộn: %d ngày (TB: %.1f phút)\n",
                    stat.getLateDaysCount(), stat.getAvgLateMinutes());
            System.out.printf("  Số ngày về sớm: %d ngày (TB: %.1f phút)\n",
                    stat.getEarlyLeaveDaysCount(), stat.getAvgEarlyLeaveMinutes());
        }

        System.out.println("\n========================================");
    }

    private boolean confirmExport(Scanner scanner) {
        System.out.print("\nXuất ra file Excel? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }
}