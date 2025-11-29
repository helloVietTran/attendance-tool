# DailyWorkTimeAnalysisService - Tài liệu chi tiết

## 📋 Tổng quan
Service này phân tích và tính toán các chỉ số thời gian làm việc hàng ngày của nhân viên dựa trên dữ liệu chấm công từ bảng `AttendanceLog` và `ProcessLog`.

---

## 🔄 Luồng xử lý chính

### 1. **Trigger Point - Điểm khởi động**
```
DataProcessingRunner.run()
  └─> dailyWorkTimeAnalysisService.processAllDailyWorkTime()
```

Service được gọi sau khi:
- ✅ Import xong dữ liệu chấm công vào `ProcessLog` và `AttendanceLog`
- ✅ Import xong lịch chốt công vào `PayRollClosing`
- ✅ Import xong ngày nghỉ vào `Holidays`

### 2. **Flow xử lý từng bước**

```
processAllDailyWorkTime()
  ├─> Lấy tất cả records từ AttendanceLog
  └─> For each AttendanceLog:
        └─> processDailyWorkTime(attendanceLog)
              ├─> [1] Validate dữ liệu (checkin/checkout không null)
              ├─> [2] Lấy thông tin ca làm việc từ ProcessLog
              ├─> [3] Tính toán các chỉ số:
              │     ├─> lateMinutes (đi muộn)
              │     ├─> earlyLeaveMinutes (về sớm)
              │     ├─> inOfficeMinutes (thời gian ở văn phòng)
              │     ├─> workTimeMinutes (thời gian làm việc thực tế)
              │     └─> lackMinutes (thiếu giờ)
              └─> [4] Lưu/Cập nhật vào bảng DailyWorkTimeAnalysis
```

---

## 🧮 Chi tiết các phép tính

### **Input Data**
Từ `AttendanceLog` và `ProcessLog`:
- `checkinTime`: Giờ check in thực tế (VD: 08:22:00)
- `checkoutTime`: Giờ check out thực tế (VD: 17:30:00)
- `shiftStartTime`: Giờ bắt đầu ca chuẩn (VD: 08:30:00)
- `shiftEndTime`: Giờ kết thúc ca chuẩn (VD: 17:30:00)

---

### **1. Late Minutes (Đi muộn)** ⏰

**Công thức:**
```java
if (checkinTime > shiftStartTime) {
    lateMinutes = checkinTime - shiftStartTime
} else {
    lateMinutes = 0
}
```

**Ví dụ:**
- Ca bắt đầu: `08:30:00`
- Check in thực tế: `08:40:00`
- ➡️ **Đi muộn: 10 phút**

---

### **2. Early Leave Minutes (Về sớm)** 🏃

**Công thức:**
```java
if (checkoutTime < shiftEndTime) {
    earlyLeaveMinutes = shiftEndTime - checkoutTime
} else {
    earlyLeaveMinutes = 0
}
```

**Ví dụ:**
- Ca kết thúc: `17:30:00`
- Check out thực tế: `17:20:00`
- ➡️ **Về sớm: 10 phút**

---

### **3. In Office Minutes (Thời gian ở văn phòng)** 🏢

**Công thức:**
```java
inOfficeMinutes = checkoutTime - checkinTime
```

**Ví dụ:**
- Check in: `08:22:00`
- Check out: `17:30:00`
- ➡️ **Ở văn phòng: 548 phút (9h 8m)**

---

### **4. Work Time Minutes (Thời gian làm việc thực tế)** 💼

**Logic phức tạp nhất** - Cần xử lý giờ nghỉ trưa:

#### **Điều kiện có nghỉ trưa:**
```java
boolean hasLunchBreak = (checkinTime <= 12:00) && (checkoutTime >= 13:00)
```

Nghỉ trưa được tính khi:
- ✅ Check in **TRƯỚC hoặc ĐÚNG 12:00**
- ✅ Check out **SAU hoặc ĐÚNG 13:00**

#### **Công thức:**
```java
if (hasLunchBreak) {
    workTimeMinutes = inOfficeMinutes - 60  // Trừ 1 giờ nghỉ trưa
} else {
    workTimeMinutes = inOfficeMinutes
}
```

#### **Các trường hợp cụ thể:**

| Check In | Check Out | Có nghỉ trưa? | Tính toán |
|----------|-----------|---------------|-----------|
| 08:00 | 17:00 | ✅ CÓ | 540 phút - 60 = **480 phút (8h)** |
| 08:00 | 12:30 | ❌ KHÔNG | 270 phút = **270 phút (4.5h)** |
| 13:00 | 17:00 | ❌ KHÔNG | 240 phút = **240 phút (4h)** |
| 12:30 | 17:00 | ❌ KHÔNG | 270 phút = **270 phút (4.5h)** |
| 11:00 | 14:00 | ✅ CÓ | 180 phút - 60 = **120 phút (2h)** |

**Lưu ý quan trọng:**
- Giờ nghỉ trưa cố định: **12:00 - 13:00**
- Chỉ trừ 1 giờ nếu khoảng làm việc **BAO GỒM** khoảng 12:00-13:00
- Không trừ nếu check in sau 12:00 hoặc check out trước 13:00

---

### **5. Lack Minutes (Thiếu giờ)** ⚠️

**Công thức:**
```java
standardWorkTime = 480 phút (8 giờ)
lackMinutes = max(0, standardWorkTime - workTimeMinutes)
```

**Ví dụ:**
- Thời gian làm việc thực tế: `450 phút` (7.5h)
- ➡️ **Thiếu: 30 phút**

Nếu `workTimeMinutes >= 480` → `lackMinutes = 0`

---

### **6. Overtime Minutes (Tăng ca)** 🌙

**Xử lý:**
```java
overTimeMinutes = 0  // Luôn luôn = 0, không tính toán
```
Theo yêu cầu, cột này để trống và không xử lý.

---

## 📊 Ví dụ tổng hợp

### **Case 1: Nhân viên làm đầy đủ ca**
```
Input:
- shiftStartTime: 08:30:00
- shiftEndTime: 17:30:00
- checkinTime: 08:20:00 (sớm 10 phút)
- checkoutTime: 17:35:00 (muộn 5 phút)

Tính toán:
✅ lateMinutes = 0 (đến sớm)
✅ earlyLeaveMinutes = 0 (về muộn)
✅ inOfficeMinutes = 555 phút (9h 15m)
✅ hasLunchBreak = true (08:20 < 12:00 và 17:35 > 13:00)
✅ workTimeMinutes = 555 - 60 = 495 phút (8h 15m)
✅ lackMinutes = 0 (làm đủ 8h)

Output → DailyWorkTimeAnalysis record được lưu với các giá trị trên
```

### **Case 2: Nhân viên đi muộn, về sớm**
```
Input:
- shiftStartTime: 08:30:00
- shiftEndTime: 17:30:00
- checkinTime: 08:45:00 (muộn 15 phút)
- checkoutTime: 17:00:00 (sớm 30 phút)

Tính toán:
✅ lateMinutes = 15 phút
✅ earlyLeaveMinutes = 30 phút
✅ inOfficeMinutes = 495 phút (8h 15m)
✅ hasLunchBreak = true (08:45 < 12:00 và 17:00 > 13:00)
✅ workTimeMinutes = 495 - 60 = 435 phút (7h 15m)
✅ lackMinutes = 480 - 435 = 45 phút

Output → Thiếu 45 phút so với 8h chuẩn
```

### **Case 3: Làm nửa ngày sáng (không có nghỉ trưa)**
```
Input:
- shiftStartTime: 08:30:00
- shiftEndTime: 17:30:00
- checkinTime: 08:30:00
- checkoutTime: 12:00:00

Tính toán:
✅ lateMinutes = 0
✅ earlyLeaveMinutes = 330 phút (5h 30m)
✅ inOfficeMinutes = 210 phút (3h 30m)
✅ hasLunchBreak = false (checkout KHÔNG >= 13:00)
✅ workTimeMinutes = 210 phút (KHÔNG trừ nghỉ trưa)
✅ lackMinutes = 480 - 210 = 270 phút

Output → Thiếu 270 phút (4.5h)
```

### **Case 4: Làm nửa ngày chiều (không có nghỉ trưa)**
```
Input:
- shiftStartTime: 08:30:00
- shiftEndTime: 17:30:00
- checkinTime: 13:00:00
- checkoutTime: 17:30:00

Tính toán:
✅ lateMinutes = 270 phút (4h 30m)
✅ earlyLeaveMinutes = 0
✅ inOfficeMinutes = 270 phút (4h 30m)
✅ hasLunchBreak = false (checkin KHÔNG <= 12:00)
✅ workTimeMinutes = 270 phút (KHÔNG trừ nghỉ trưa)
✅ lackMinutes = 480 - 270 = 210 phút

Output → Thiếu 210 phút (3.5h)
```

---

## 💾 Database Operations

### **Insert hoặc Update**
```java
DailyWorkTimeAnalysis analysis = repository
    .findByEmployeeIdAndWorkDate(employeeId, workDate)
    .orElse(new DailyWorkTimeAnalysis());

// Set các giá trị...
repository.save(analysis);
```

**Logic:**
- Nếu đã tồn tại record cho `employeeId` + `workDate` → **Update**
- Nếu chưa có → **Insert** mới

### **Timestamps tự động**
```java
@PrePersist: createdAt = now(), updatedAt = now()
@PreUpdate: updatedAt = now()
```

---

## 🔍 Edge Cases & Validations

### **1. Thiếu dữ liệu**
```java
if (checkinTime == null || checkoutTime == null) {
    return; // Bỏ qua, không xử lý
}
```

### **2. Không tìm thấy ProcessLog**
```java
if (processLogIn == null) {
    return; // Bỏ qua, không xử lý
}
```

### **3. Checkout trước Checkin (dữ liệu lỗi)**
Hiện tại chưa validate, nhưng `Duration.between()` sẽ trả về số âm.
**Khuyến nghị:** Thêm validation:
```java
if (checkoutTime.isBefore(checkinTime)) {
    throw new IllegalArgumentException("Checkout time cannot be before checkin time");
}
```

---

## 🎯 Kết quả cuối cùng

Mỗi record trong `AttendanceLog` sẽ tạo 1 record tương ứng trong `DailyWorkTimeAnalysis` với đầy đủ các chỉ số:

| Cột | Mô tả | Đơn vị |
|-----|-------|--------|
| employeeId | ID nhân viên | int |
| workDate | Ngày làm việc | DATE |
| shiftStartTime | Giờ bắt đầu ca | TIME |
| shiftEndTime | Giờ kết thúc ca | TIME |
| lateMinutes | Số phút đi muộn | phút |
| earlyLeaveMinutes | Số phút về sớm | phút |
| inOfficeMinutes | Thời gian ở văn phòng | phút |
| workTimeMinutes | Thời gian làm việc thực tế | phút |
| lackMinutes | Số phút thiếu giờ | phút |
| overTimeMinutes | Tăng ca (luôn = 0) | phút |

---

## 🚀 Usage

```java
// Xử lý tất cả
dailyWorkTimeAnalysisService.processAllDailyWorkTime();

// Xử lý 1 record cụ thể
AttendanceLog log = attendanceLogRepository.findById(...);
dailyWorkTimeAnalysisService.processDailyWorkTime(log);
```

---

## 📝 Notes

- ⚠️ **Giờ nghỉ trưa cố định:** 12:00 - 13:00 (1 giờ)
- ⚠️ **Giờ làm việc chuẩn:** 8 giờ (480 phút)
- ⚠️ Service chạy sau khi import xong dữ liệu chấm công
- ⚠️ Overtime không được tính toán (cột = 0)
- ✅ Hỗ trợ cả insert và update (upsert based on employeeId + workDate)
- ✅ Timestamp tự động (createdAt, updatedAt)
