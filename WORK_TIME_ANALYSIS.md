# 📊 Work Time Analysis - Phân Tích Thời Gian Làm Việc

## 🎯 Mục Đích

Module này phân tích dữ liệu chấm công từ file Excel và tính toán các chỉ số:
- ⏰ Số phút đi muộn
- 🏃 Số phút về sớm  
- 📉 Số phút thiếu
- ⏱️ Số phút làm thêm (OT)
- 🏢 Tổng thời gian có mặt
- 💼 Thời gian làm việc thực tế

---

## 📋 Quy Định Công Ty

| Quy định | Giá trị |
|----------|---------|
| **Ca làm việc** | 8:30 - 17:30 |
| **Nghỉ trưa** | 1 giờ (60 phút) |
| **Thời gian làm việc** | 8 giờ (480 phút) |

---

## 🗄️ Database Setup

### Tạo bảng:

```sql
CREATE TABLE DailyWorkTimeAnalysis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT NOT NULL,
    workDate DATE NOT NULL,
    lateMinutes INT DEFAULT 0,
    earlyLeaveMinutes INT DEFAULT 0,
    lackMinutes INT DEFAULT 0,
    overTimeMinutes INT DEFAULT 0,
    inOfficeMinutes INT DEFAULT 0,
    workTimeMinutes INT DEFAULT 0,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    shiftStartTime TIME NOT NULL,
    shiftEndTime TIME NOT NULL
);
```

Hoặc chạy file SQL:
```bash
mysql -u root -p attendance_tool < src/main/resources/sql/create_daily_work_time_analysis.sql
```

---

## 🔄 Cách Hoạt Động

### Input: File Excel `cham-cong.xlsx`

| ID | EMPLOYEE ID | DATE | CHECKED TIME |
|----|-------------|------|--------------|
| 93001 | 1 | 19/11/2025 | 8:10 |
| 93015 | 1 | 19/11/2025 | 16:45 |

### Processing:

```
Nhân viên ID: 1
Ngày: 19/11/2025
Checkin: 8:10
Checkout: 16:45

Tính toán:
├── Đi muộn: 0 phút (8:10 < 8:30 ✅)
├── Về sớm: 45 phút (16:45 < 17:30 ❌)
├── Có mặt: 515 phút (8:10 → 16:45)
├── Làm việc: 455 phút (515 - 60 nghỉ trưa)
└── Thiếu: 25 phút (480 - 455)
```

### Output: Bảng `DailyWorkTimeAnalysis`

| employeeId | workDate | lateMinutes | earlyLeaveMinutes | lackMinutes | workTimeMinutes |
|------------|----------|-------------|-------------------|-------------|-----------------|
| 1 | 2025-11-19 | 0 | 45 | 25 | 455 |

---

## 📊 Ví Dụ Tính Toán

### Case 1: Làm việc bình thường
```
Checkin:  8:30
Checkout: 17:30
─────────────────────
Đi muộn:     0 phút
Về sớm:      0 phút
Có mặt:    540 phút (9 giờ)
Làm việc:  480 phút (8 giờ)
Thiếu:       0 phút
OT:          0 phút
```

### Case 2: Đi muộn, về sớm
```
Checkin:  9:00  (muộn 30 phút)
Checkout: 17:00 (sớm 30 phút)
─────────────────────
Đi muộn:    30 phút
Về sớm:     30 phút
Có mặt:    480 phút (8 giờ)
Làm việc:  420 phút (7 giờ)
Thiếu:      60 phút
OT:          0 phút
```

### Case 3: Làm thêm giờ (OT)
```
Checkin:  8:00  (sớm 30 phút)
Checkout: 18:30 (muộn 60 phút)
─────────────────────
Đi muộn:     0 phút
Về sớm:      0 phút
Có mặt:    630 phút (10.5 giờ)
Làm việc:  570 phút (9.5 giờ)
Thiếu:       0 phút
OT:         90 phút (1.5 giờ)
```

---

## 🚀 Cách Sử Dụng

### 1. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

### 2. Kiểm tra kết quả:
```sql
SELECT * FROM DailyWorkTimeAnalysis 
WHERE workDate = '2025-11-19';
```

### 3. Báo cáo tổng hợp:
```sql
-- Tổng hợp theo nhân viên
SELECT 
    employeeId,
    COUNT(*) as workDays,
    SUM(lateMinutes) as totalLate,
    SUM(earlyLeaveMinutes) as totalEarlyLeave,
    SUM(lackMinutes) as totalLack,
    SUM(overTimeMinutes) as totalOT,
    AVG(workTimeMinutes) as avgWorkTime
FROM DailyWorkTimeAnalysis
WHERE workDate BETWEEN '2025-11-01' AND '2025-11-30'
GROUP BY employeeId;
```

---

## 📁 Cấu Trúc Code

```
src/main/java/com/kits/tool/
├── entity/
│   └── DailyWorkTimeAnalysis.java       # Entity
├── repository/
│   └── DailyWorkTimeAnalysisRepository.java  # Repository
├── service/
│   └── WorkTimeAnalysisService.java     # Business Logic
└── ToolApplication.java                 # Entry Point
```

---

## 🔍 Logic Tính Toán

### 1. Đi muộn (Late Minutes)
```java
if (checkin > 8:30) {
    lateMinutes = checkin - 8:30
}
```

### 2. Về sớm (Early Leave Minutes)
```java
if (checkout < 17:30) {
    earlyLeaveMinutes = 17:30 - checkout
}
```

### 3. Thời gian có mặt (In Office Minutes)
```java
inOfficeMinutes = checkout - checkin
```

### 4. Thời gian làm việc (Work Time Minutes)
```java
workTimeMinutes = inOfficeMinutes - 60 (nghỉ trưa)
```

### 5. Thiếu hoặc OT
```java
difference = workTimeMinutes - 480

if (difference < 0) {
    lackMinutes = |difference|
    overTimeMinutes = 0
} else {
    lackMinutes = 0
    overTimeMinutes = difference
}
```

---

## ✅ Checklist

- [x] Tạo Entity `DailyWorkTimeAnalysis`
- [x] Tạo Repository
- [x] Tạo Service phân tích
- [x] Tích hợp vào `ToolApplication`
- [x] Tạo SQL script
- [x] Viết documentation

---

## 🎉 Kết Quả

Sau khi chạy, bạn sẽ có:
- ✅ Dữ liệu phân tích chi tiết từng ngày
- ✅ Dễ dàng tạo báo cáo tháng/quý/năm
- ✅ Phát hiện nhân viên đi muộn/về sớm
- ✅ Tính lương chính xác dựa trên giờ công

