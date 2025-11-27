# 🔧 Fix Lỗi Lệch 7 Phút (Timezone Issue)

## ❌ **Vấn Đề:**

```
Excel: 23:59
  ↓
Parse sang Date object
  ↓
Kết quả: 23:52 (lệch 7 phút!)
```

---

## 🔍 **Nguyên Nhân Chi Tiết:**

### **1. Excel Date Serial Number**

Excel lưu thời gian dưới dạng số thập phân:

| Thời gian | Excel Value | Giải thích |
|-----------|-------------|------------|
| 00:00:00 | 0.0 | Nửa đêm |
| 12:00:00 | 0.5 | Giữa trưa |
| 23:59:00 | 0.999305556 | Gần nửa đêm |

### **2. Excel Epoch vs Java Epoch**

```
Excel: Tính từ 1899-12-31
Java:  Tính từ 1970-01-01
```

Khi convert giữa 2 hệ thống → Có thể bị lệch!

### **3. Timezone GMT+7 (Việt Nam)**

```java
// Code cũ:
@ExcelProperty("CHECKED TIME")
@DateTimeFormat("HH:mm")
private Date checkedTime;  // ❌ Date có timezone!

// Flow:
Excel: 23:59
  ↓
EasyExcel parse: Date object (có timezone GMT+7)
  ↓
SimpleDateFormat.format(): "23:52" (lệch 7 phút!)
```

### **4. Floating Point Precision**

```
0.999305556 (Excel)
  ↓ Convert
  ↓ Có thể bị làm tròn
  ↓
0.993055556 (lệch)
  ↓
23:52 (lệch 7 phút)
```

---

## ✅ **Giải Pháp:**

### **Thay đổi:**

```java
// TRƯỚC (❌):
@ExcelProperty("CHECKED TIME")
@DateTimeFormat("HH:mm")
private Date checkedTime;  // Date có timezone, gây lệch

// SAU (✅):
@ExcelProperty("CHECKED TIME")
@DateTimeFormat("HH:mm")
private String checkedTime;  // String không có timezone, chính xác!
```

---

## 📊 **So Sánh:**

### **Cách Cũ (Date):**
```
Excel: 23:59
  ↓
EasyExcel: Date(1899-12-31 23:59:00 GMT+7)
  ↓
SimpleDateFormat: "23:52"  ❌ Lệch!
  ↓
LocalTime.parse("23:52")
  ↓
Kết quả: 23:52
```

### **Cách Mới (String):**
```
Excel: 23:59
  ↓
EasyExcel: "23:59"  ✅ Chính xác!
  ↓
LocalTime.parse("23:59")
  ↓
Kết quả: 23:59
```

---

## 🔧 **Các File Đã Sửa:**

### **1. ExcelRowDTO.java**
```java
// Đổi từ Date → String
private String checkedTime;
```

### **2. AttendanceTransformer.java**
```java
// Trước:
String timeStr = timeFormat.format(rawData.getCheckedTime());

// Sau:
String timeStr = rawData.getCheckedTime();  // Không cần format
```

### **3. LogService.java**
```java
// Trước:
dbRawLog.setCheckedTime(LocalTime.parse(sdf.format(rawData.getCheckedTime()),timeFmt));

// Sau:
dbRawLog.setCheckedTime(LocalTime.parse(rawData.getCheckedTime(),timeFmt));
```

---

## 🎯 **Kết Quả:**

### **Trước fix:**
```
👤 Nhân viên 67 - Ngày 26/11/2025 - Checkout: 23:52  ❌
✅ Tổng số trường hợp quên checkout: 0  ❌
```

### **Sau fix:**
```
👤 Nhân viên 67 - Ngày 26/11/2025 - Checkout: 23:59  ✅
⚠️ Phát hiện quên checkout: Nhân viên 67 ngày 2025-11-26  ✅
✅ Tổng số trường hợp quên checkout: 1  ✅
```

---

## 💡 **Bài Học:**

### **Khi làm việc với Excel + Java:**

❌ **Tránh:**
- Dùng `Date` cho time-only values
- Dùng `SimpleDateFormat` với Date có timezone
- Parse qua nhiều lớp (Date → String → LocalTime)

✅ **Nên:**
- Dùng `String` cho time values
- Parse trực tiếp String → LocalTime
- Tránh timezone conversion không cần thiết

---

## 🔍 **Debug Tips:**

Nếu gặp lỗi tương tự:

```java
// In ra để debug:
System.out.println("Excel raw: " + rawData.getCheckedTime());
System.out.println("After parse: " + timeStr);
System.out.println("LocalTime: " + LocalTime.parse(timeStr));
```

---

## ✅ **Checklist:**

- [x] Đổi `Date` → `String` trong ExcelRowDTO
- [x] Bỏ `SimpleDateFormat.format()` trong AttendanceTransformer
- [x] Sửa `LogService` parse trực tiếp
- [x] Test với dữ liệu thật
- [x] Verify 23:59 không bị lệch

---

Bây giờ `23:59` sẽ chính xác 100%! 🎉

