# 📧 Hướng Dẫn Cấu Hình Email

## 🎯 Cách Lấy App Password (Gmail)

### **Bước 1: Bật 2-Step Verification**
1. Vào: https://myaccount.google.com/security
2. Tìm **2-Step Verification**
3. Bật lên nếu chưa có

### **Bước 2: Tạo App Password**
1. Vào: https://myaccount.google.com/apppasswords
2. Chọn:
   - **Select app**: Mail
   - **Select device**: Other (Custom name)
3. Đặt tên: `Attendance Tool`
4. Click **Generate**
5. Copy mật khẩu 16 ký tự (vd: `abcd efgh ijkl mnop`)

---

## ⚙️ Cấu Hình application.yaml

### **Mẫu cấu hình:**

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-real-email@gmail.com      # ← Email thật của bạn
    password: abcd efgh ijkl mnop            # ← App password (16 ký tự)
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
    default-encoding: UTF-8
```

### **Ví dụ thực tế:**

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: nguyenvana@gmail.com           # Email của bạn
    password: abcd efgh ijkl mnop            # App password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
    default-encoding: UTF-8
```

---

## 🧪 Test Gửi Email

### **Tạo file test:**

```java
// Test gửi email đơn giản
CheckInErrorDTO dto = new CheckInErrorDTO();
dto.setUserId("TEST");
dto.setUsername("Test User");
dto.setEmail("recipient@example.com");  // Email người nhận
dto.setStartWorkingShift("08:30");
dto.setEndWorkingShift("17:30");
dto.setDate(LocalDate.now());
dto.setFirstCheckInTime(LocalTime.of(23, 59));
dto.setLastCheckInTime(LocalTime.of(23, 59));

mailService.sendHtmlEmail(
    dto.getEmail(),
    "Test Email",
    "check-in-error-mail",
    dto
);
```

---

## 🔍 Troubleshooting

### **Lỗi: Authentication failed**
- ✅ Check email đúng chưa
- ✅ Check app password đúng chưa (16 ký tự, không có khoảng trắng)
- ✅ Đã bật 2-Step Verification chưa

### **Lỗi: Connection timeout**
- ✅ Check port 587
- ✅ Check firewall/antivirus
- ✅ Thử đổi sang port 465 (SSL)

### **Lỗi: Invalid Addresses**
- ✅ Check email người nhận đúng format
- ✅ Check không có ký tự đặc biệt

---

## 📝 Lưu Ý Bảo Mật

⚠️ **KHÔNG commit app password lên Git!**

### **Cách 1: Dùng environment variables**

```yaml
spring:
  mail:
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

Chạy:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
mvn spring-boot:run
```

### **Cách 2: Dùng file riêng (không commit)**

Tạo `application-local.yaml`:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

Thêm vào `.gitignore`:
```
application-local.yaml
```

Chạy:
```bash
mvn spring-boot:run -Dspring.profiles.active=local
```

---

## ✅ Checklist

- [ ] Đã tạo App Password
- [ ] Đã cập nhật application.yaml
- [ ] Đã test gửi email
- [ ] Email gửi thành công
- [ ] Không commit password lên Git

---

## 🎉 Kết Quả

Sau khi cấu hình đúng, email sẽ được gửi tự động mỗi khi tool chạy!

```
Console output:
✅ Đã gửi email lần 1 cho nhân viên 19 (sự cố ngày 2025-11-21)
```

Email sẽ đến hộp thư của nhân viên với nội dung:
```
Dear Đặng Khánh Vy, ID: 19

⛔ Quên check out / Chỉ có 1 lần check in
Lần check in ghi nhận: 23:59

Vui lòng chú ý thời gian làm việc!
```

