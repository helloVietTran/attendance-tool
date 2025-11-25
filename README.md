## Feat/read-file

#### Main Part:
- Save data from log to DB
- Save payroll closing date data to DB
- Save holiday date data to DB
- Create an CalendarService for validating a day if it is a holiday.
- Database Schema
---

#### General Process:
- Luồng chính sẽ chạy từ **ToolMain**
- Đọc file xlsx với `EasyExcel.read()`
  - EasyExcel sử dụng annotation `@ExcelProperty()` để xác định các cột trong file
- Gán dữ liệu vừa đọc vào một List<> để xử lý
- Dùng `SimpleDateFormat` để chuyển dữ liệu từ **Date** về **String**
- Dùng `LocalDate.parse()` để chuyển dữ liệu dạng **String** sang **LocalDate** theo format `DateTimeFormatter.ofPattern()` cho việc ánh xạ vào DB   
- Package:
  - dto: dùng để ánh xạ dữ liệu đọc hoặc ghi từ file excel 
  - entity: ánh xạ dữ liệu sau khi đọc DB
  - service và repository: thực hiện thao tác với DB
  - excel_process: gồm **ToolMain** làm entry point chạy chương trình, AttendanceTransform để xử lý chuyển đổi data log và các listener để validate data khi đọc (bỏ qua)

#### Save data from log to DB:
- Đọc file input gốc và gán vào một List<>
- Dùng `ExcelRowDTO` và `ExcelExportDTO` đọc file
- Dùng `DatabaseRowDTO` và `DatabaseExportDTO` làm entity ánh xạ qua DB  
- Thực hiện chuyển đổi dữ liệu từ dạng gốc về dạng dễ đọc hơn: `AttendanceTransformer.transform(rawData)` 
  - `AttendanceTransformer` dùng `LinkedHashMap<>` để vừa lưu data theo key và vừa giữ thứ tự xuất hiện
  -  Duyệt qua các bản ghi theo composite key **(empId và Date)** và lưu data vào map vừa tạo
-  LogService thực hiện lưu data vào DB
  - `saveRawFromExcel`: Lưu dữ liệu gốc vào bảng ProcessLog theo id log gốc `processLogRepository.findById(dbRawLog.getId());`
  - `saveNewFromExcel`: Lưu dữ liệu theo format mới vào bảng AttendanceLog

#### Save payroll closing date data to DB
- Thực hiện đọc file và lưu List<>
- `YearMonthClosingDTO` để đọc file và `DateClosingDTO` để chuyển dữ liệu rời rạc đã đọc về dữ liệu chuẩn yyyy-MM-dd
- `PayrollClosingEntity` để ánh xạ vào DB
- `PayrollClosingService` thực hiện lưu data ngày chốt công vào bảng PayRollClosing trong DB
  - Với format file mẫu và điều kiện do người dùng nhập, `fromRawToDate` thực hiện đọc data và xử lý ngày nhập hợp lệ theo các tháng cho từng bản ghi
  - `saveToDb`: xử lý lưu data vào DB
    - Do dữ liệu mẫu có giới hạn đầu nên thực hiện xử lý *ngày bắt đầu tính công* theo 2 loại: ngày đầu tiên của tháng với tháng đầu tiên `firstDayofMonth` và sau 1 ngày từ ngày chốt công với các tháng còn lại `previousPrcDate.plusDays(1);`
    - Với ngày chốt công thì lưu bình thường, data được lưu theo id là `List<>.indexOf() +1`

#### Save holiday date data to DB
- Solution: chia làm 3 loại ngày nghỉ
  - **Public**: Nghỉ của cả nước => Import từ file
  - **Company**: Nghỉ của công ty => Xử lý với code
  - **Others**: Nghỉ khác => Import từ file
- `HolidayDTO` để đọc file public và others
- `HolidayEntity` để ánh xạ vào DB
- `HolidayImportService` xử lý đọc file và lưu vào DB:
  - `importFromExcel`: đọc file và lưu vào DB theo các cột trong **HolidayEntity**
  - `generateWeekendHolidays`: tìm ngày nghỉ thứ 7 và chủ nhật của cả năm, mặc định `type` là **Company** và `detail` là **Weekend Day**
 
#### Create an CalendarService for validating a day if it is a holiday
- Điều kiện tiên quyết: có bảng **Holidays** trong DB
- Service này để xác định một ngày có phải ngày nghỉ hay không từ bảng **Holidays** 
- `isHoliday(LocalDate date)`: check xem LocalDate truyền vào có phải ngày nghỉ hay không bằng cách dùng **query method** `holidayRepository.existsByHolidayDate(date)` => tự động sinh SQL ~ `SELECT EXISTS(SELECT 1 FROM holiday WHERE holiday_date = ?);`

#### Database Query
```
create table Employee (
	  id int primary key
);
```

```
create table ProcessLog(
  id int primary key,
    employeeId int,
    processDate date not null,
    startTime datetime,
    endTime datetime,
    checkedTime datetime
);
```


```
create table AttendanceLog (
    employeeId int not null,
    processLogInId int, 
    processLogOutId int,
    processDate date not null,
    checkinTime datetime,
    checkoutTime datetime,
    primary key(employeeId, processDate),
  foreign key (processLogInId) references ProcessLog(id) 
    on delete cascade
    on update cascade,
    foreign key (processLogOutId) references ProcessLog(id) 
    on delete cascade
    on update cascade
);
```

```
create table Holidays (
  id int auto_increment primary key,
  holidayDate date not null,
  detail varchar(100), 
  type enum('Public', 'Company', 'Others') default 'Public' -- Public: Nghỉ lễ cả nước; Company: do cty quy định; Others: khác
);
```

```
create table PayRollClosing (
    id int primary key,       
    periodStart date not null, 				-- Ngày bắt đầu kỳ công
    periodEnd date not null,				-- Ngày chốt công
    created_at timestamp default current_timestamp, -- Thời gian tạo bản ghi
    updated_at timestamp default current_timestamp on update current_timestamp-- Thời gian cập nhật bản ghi
);
```

```
create table LateEarlyPermitted (
  allowedLateMinute int,
  allowedEarlyLeaveMinute int,
  LateCheckinCount int, 
  EarlyLeaveCount int
);
```
