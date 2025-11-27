# 📧 Forgot Checkout Email Notification System

## 🎯 Mục Đích

Hệ thống tự động phát hiện và gửi email cảnh báo cho nhân viên **quên checkout**, gửi liên tiếp **3 ngày**, mỗi ngày 1 lần.

---

## 📋 Quy Định

| Quy tắc | Giá trị |
|---------|---------|
| **Phát hiện quên checkout** | Checkout time = 23:59 |
| **Số lần gửi email** | Tối đa 3 lần |
| **Tần suất** | Mỗi ngày 1 lần |
| **Bắt đầu gửi** | Từ ngày hôm sau sự cố |

---

## 🔄 Cách Hoạt Động

### **Flow Tổng Quan:**

```
Ngày 21/11: Nhân viên quên checkout (23:59)
    ↓
Ngày 22/11: Tool chạy → Phát hiện → Gửi email lần 1
    ↓
Ngày 23/11: Tool chạy → Gửi email lần 2
    ↓
Ngày 24/11: Tool chạy → Gửi email lần 3
    ↓
Ngày 25/11: Tool chạy → Không gửi (đã đủ 3 lần)
```

---

## 🗄️ Database Setup

### Tạo bảng:

```sql
CREATE TABLE ForgotCheckoutEmailLog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT NOT NULL,
    incidentDate DATE NOT NULL,
    emailSentCount INT DEFAULT 0,
    lastSentDate DATE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_employee_incident (employeeId, incidentDate)
);
```

Hoặc chạy:
```bash
mysql -u root -p attendance_tool < src/main/resources/sql/create_forgot_checkout_email_log.sql
```

---

## 📊 Ví Dụ Chi Tiết

### **Scenario: Nhân viên ID=19 quên checkout ngày 21/11/2025**

#### **Dữ liệu Excel:**
```
ID: 93040
Employee ID: 19
Date: 21/11/2025
Checked Time: 23:59  ← Quên checkout!
```

#### **Xử lý:**

**Ngày 21/11 (23:00) - Tool chạy:**
```
1. detectForgotCheckout() phát hiện checkout = 23:59
2. Tạo log trong ForgotCheckoutEmailLog:
   - employeeId: 19
   - incidentDate: 2025-11-21
   - emailSentCount: 0
   - lastSentDate: NULL
```

**Ngày 22/11 (08:00) - Tool chạy:**
```
1. sendPendingNotifications() check:
   - today (22/11) > incidentDate (21/11) ✅
   - emailSentCount (0) < 3 ✅
   - lastSentDate = NULL ✅
   
2. Gửi email lần 1
3. Cập nhật log:
   - emailSentCount: 1
   - lastSentDate: 2025-11-22
   
Console: ✅ Đã gửi email lần 1 cho nhân viên 19 (sự cố ngày 2025-11-21)
```

**Ngày 23/11 (08:00) - Tool chạy:**
```
1. sendPendingNotifications() check:
   - today (23/11) > lastSentDate (22/11) ✅
   - emailSentCount (1) < 3 ✅
   
2. Gửi email lần 2
3. Cập nhật log:
   - emailSentCount: 2
   - lastSentDate: 2025-11-23
   
Console: ✅ Đã gửi email lần 2 cho nhân viên 19 (sự cố ngày 2025-11-21)
```

**Ngày 24/11 (08:00) - Tool chạy:**
```
1. sendPendingNotifications() check:
   - today (24/11) > lastSentDate (23/11) ✅
   - emailSentCount (2) < 3 ✅
   
2. Gửi email lần 3
3. Cập nhật log:
   - emailSentCount: 3
   - lastSentDate: 2025-11-24
   
Console: ✅ Đã gửi email lần 3 cho nhân viên 19 (sự cố ngày 2025-11-21)
```

**Ngày 25/11 (08:00) - Tool chạy:**
```
1. sendPendingNotifications() check:
   - emailSentCount (3) < 3 ❌ (đã đủ 3 lần)
   
2. Không gửi email
3. Không có log
```

---

## 📧 Email Template

Email sử dụng template `check-in-error-mail.html`:

```html
<!-- CASE: Quên check out -->
<div th:if="${firstCheckInTime != null
            and lastCheckInTime != null
            and firstCheckInTime.equals(lastCheckInTime)}">
    <p class="section-title">⛔ Quên check out / Chỉ có 1 lần check in</p>
    <p>
        Lần check in ghi nhận:
        <span class="error-detail" th:text="${firstCheckInTime}"></span>
    </p>
</div>
```

**Nội dung email:**
```
Dear Đặng Khánh Vy, ID: 19

Khung làm việc: 08:30 - 17:30

Hệ thống ghi nhận lỗi Timesheet trong ngày: 21/11/2025.

⛔ Quên check out / Chỉ có 1 lần check in
Lần check in ghi nhận: 23:59

Vui lòng chú ý thời gian làm việc để đảm bảo đủ công trong tháng!
```

---

## 🔍 Logic Chi Tiết

### **1. Phát hiện quên checkout:**
```java
if (checkoutTime.equals(LocalTime.of(23, 59))) {
    // Tạo log trong ForgotCheckoutEmailLog
    // emailSentCount = 0
}
```

### **2. Điều kiện gửi email:**
```java
boolean shouldSend = 
    (lastSentDate == null || lastSentDate.isBefore(today))  // Chưa gửi hôm nay
    && today.isAfter(incidentDate)                          // Sau ngày sự cố
    && emailSentCount < 3;                                  // Chưa đủ 3 lần
```

### **3. Gửi email và cập nhật:**
```java
sendForgotCheckoutEmail(log, employeeInfo);
log.setEmailSentCount(log.getEmailSentCount() + 1);
log.setLastSentDate(today);
emailLogRepository.save(log);
```

---

## 📊 Bảng ForgotCheckoutEmailLog

### **Ví dụ dữ liệu:**

| id | employeeId | incidentDate | emailSentCount | lastSentDate | createdAt | updatedAt |
|----|------------|--------------|----------------|--------------|-----------|-----------|
| 1 | 19 | 2025-11-21 | 3 | 2025-11-24 | 2025-11-21 23:00 | 2025-11-24 08:00 |
| 2 | 5 | 2025-11-22 | 1 | 2025-11-23 | 2025-11-22 23:00 | 2025-11-23 08:00 |

---

## 🚀 Cách Sử Dụng

### **1. Tạo bảng trong MySQL:**
```bash
mysql -u root -p1 -D attendance_tool < src/main/resources/sql/create_forgot_checkout_email_log.sql
```

### **2. Chạy tool:**
```bash
mvn spring-boot:run
```

### **3. Kiểm tra log:**
```sql
SELECT * FROM ForgotCheckoutEmailLog;
```

### **4. Xem nhân viên nào đang pending:**
```sql
SELECT * FROM ForgotCheckoutEmailLog 
WHERE emailSentCount < 3;
```

---

## 🎯 Tính Năng

✅ **Tự động phát hiện** quên checkout (23:59)  
✅ **Gửi email tự động** mỗi ngày  
✅ **Giới hạn 3 lần** để tránh spam  
✅ **Track lịch sử** gửi email  
✅ **Không gửi trùng** trong cùng 1 ngày  

---

## 📁 Cấu Trúc Code

```
src/main/java/com/kits/tool/
├── entity/
│   └── ForgotCheckoutEmailLog.java          # Entity
├── repository/
│   └── ForgotCheckoutEmailLogRepository.java # Repository
├── service/
│   └── ForgotCheckoutNotificationService.java # Business Logic
├── dto/
│   └── CheckInErrorDTO.java                  # DTO cho email
└── ToolApplication.java                      # Entry Point
```

---

## ⚠️ Lưu Ý

1. **Cấu hình SMTP** trong `application.yaml` để gửi email thực
2. **Chạy tool mỗi ngày** để hệ thống hoạt động đúng
3. **Unique constraint** đảm bảo không tạo log trùng
4. **Email chỉ gửi từ ngày hôm sau** sự cố, không gửi ngay

---

## 🔧 Troubleshooting

### **Email không gửi?**
- Check SMTP config trong `application.yaml`
- Check log console: `✅ Đã gửi email lần X`

### **Gửi nhiều hơn 3 lần?**
- Check `emailSentCount` trong database
- Check logic `emailSentCount < 3`

### **Không phát hiện quên checkout?**
- Check dữ liệu Excel: checkout phải = 23:59
- Check console: `⚠️ Phát hiện quên checkout`

---

Hệ thống đã sẵn sàng! 🎉

