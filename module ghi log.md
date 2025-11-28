# 📘 LogService -- Data Flow Documentation

Tài liệu này mô tả **luồng dữ liệu** và **quy trình xử lý** của class
`LogService` trong module xử lý log điểm danh từ file Excel.

------------------------------------------------------------------------

## 📂 **1. Giới thiệu**

`LogService` bao gồm hai chức năng chính:

1.  **saveRawFromExcel(List`<ExcelRowDTO>`{=html})**
    -   Nhận dữ liệu thô từ file Excel\
    -   Lưu vào bảng **ProcessLog**
2.  **saveNewFromExcel(List`<ExcelExportDTO>`{=html})**
    -   Nhận dữ liệu đã xử lý\
    -   Lưu vào bảng **AttendanceLog**

------------------------------------------------------------------------

## 📄 **2. Luồng xử lý chi tiết**

------------------------------------------------------------------------

# 🔵 2.1. `saveRawFromExcel()`

### **▶ Input**

Danh sách `ExcelRowDTO`, gồm các thông tin:

-   id\
-   empId\
-   startTime\
-   endTime\
-   date\
-   checkedTime

------------------------------------------------------------------------

## **📌 Quy trình xử lý**

### **Bước 1: Lặp danh sách Excel**

``` java
for (ExcelRowDTO rawData : rawDatas)
```

→ Mỗi dòng Excel được xử lý độc lập.

------------------------------------------------------------------------

### **Bước 2: Tạo mới entity ProcessLog**

``` java
ProcessLog dbRawLog = new ProcessLog();
```

------------------------------------------------------------------------

### **Bước 3: Kiểm tra bản ghi có tồn tại trong DB không**

``` java
Optional<ProcessLog> row = processLogRepository.findById(rawData.getId());
```

-   Nếu **không tồn tại** (`row.isEmpty()`):
    -   Gán dữ liệu từ Excel vào entity mới.
-   Nếu **tồn tại**:
    -   Không cập nhật, nhưng code hiện tại vẫn gọi `save()` cho object
        rỗng\
        → **Lỗi logic cần chú ý**.

------------------------------------------------------------------------

### **Bước 4: Gán dữ liệu (nếu chưa tồn tại)**

``` java
dbRawLog.setId(rawData.getId());
dbRawLog.setEmpId(rawData.getEmpId());
dbRawLog.setStartTime(rawData.getStartTime());
dbRawLog.setEndTime(rawData.getEndTime());
dbRawLog.setDate(rawData.getDate());
dbRawLog.setCheckedTime(rawData.getCheckedTime());
```

------------------------------------------------------------------------

### **Bước 5: Lưu vào ProcessLog**

``` java
processLogRepository.save(dbRawLog);
```

------------------------------------------------------------------------

## **▶ Output**

-   Lưu dữ liệu vào bảng **ProcessLog**.

------------------------------------------------------------------------

------------------------------------------------------------------------

# 🔵 2.2. `saveNewFromExcel()`

### **▶ Input**

Danh sách `ExcelExportDTO`, chứa:

-   empId\
-   checkinId\
-   checkoutId\
-   date\
-   checkinTime\
-   checkoutTime

------------------------------------------------------------------------

## **📌 Quy trình xử lý**

### **Bước 1: Lặp dữ liệu**

``` java
for (ExcelExportDTO newData : newDatas)
```

------------------------------------------------------------------------

### **Bước 2: Tạo entity AttendanceLog**

``` java
AttendanceLog dbNewLog = new AttendanceLog();
```

------------------------------------------------------------------------

### **Bước 3: Gán dữ liệu**

``` java
dbNewLog.setEmpId(newData.getEmpId());
dbNewLog.setProcessLogInId(newData.getCheckinId());
dbNewLog.setProcessLogOutId(newData.getCheckoutId());
dbNewLog.setProcessDate(newData.getDate());
dbNewLog.setCheckinTime(newData.getCheckinTime());
dbNewLog.setCheckoutTime(newData.getCheckoutTime());
```

------------------------------------------------------------------------

### **Bước 4: Lưu vào AttendanceLog**

``` java
attendanceLogRepository.save(dbNewLog);
```

------------------------------------------------------------------------

## **▶ Output**

-   Một bản ghi mới trong **AttendanceLog**.

------------------------------------------------------------------------

# 📈 **3. Sơ đồ luồng dữ liệu (Tóm tắt)**

    Excel Raw Data → ExcelRowDTO → saveRawFromExcel()
            ↓ kiểm tra tồn tại
       Chưa tồn tại → map dữ liệu → save ProcessLog
       Đã tồn tại    → object rỗng (có thể gây lỗi) → save

    Excel Processed Data → ExcelExportDTO → saveNewFromExcel()
            → map dữ liệu → save AttendanceLog

------------------------------------------------------------------------

# ⚠️ **4. Ghi chú và gợi ý cải thiện**

### ✔ Tránh lưu object rỗng khi bản ghi đã tồn tại

Thay:

``` java
if(row.isEmpty()) { ... }
processLogRepository.save(dbRawLog);
```

→ Bằng:

``` java
if (!row.isEmpty()) continue;
```

### ✔ Có thể dùng MapStruct để giảm code gán thủ công.

### ✔ Nên dùng batch insert nếu dữ liệu Excel lớn.

------------------------------------------------------------------------

# 📬 Kết luận

Tài liệu này mô tả rõ luồng xử lý dữ liệu của `LogService`, giúp dễ dàng
bảo trì, refactor hoặc trình bày trong tài liệu kỹ thuật.
