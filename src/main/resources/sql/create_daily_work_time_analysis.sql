USE attendance_tool;

DROP TABLE IF EXISTS AttendanceLog;
DROP TABLE IF EXISTS ProcessLog;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Holidays;
DROP TABLE IF EXISTS PayRollClosing;
DROP TABLE IF EXISTS DailyWorkTimeAnalysis;

-- Tạo lại với TIME thay vì DATETIME
CREATE TABLE ProcessLog(
  id INT PRIMARY KEY,
  employeeId INT,
  processDate DATE NOT NULL,
  startTime TIME,           -- ✅ TIME thay vì DATETIME
  endTime TIME,             -- ✅ TIME thay vì DATETIME
  checkedTime TIME          -- ✅ TIME thay vì DATETIME
);

CREATE TABLE AttendanceLog (
  employeeId INT NOT NULL,
  processLogInId INT, 
  processLogOutId INT,
  processDate DATE NOT NULL,
  checkinTime TIME,         -- ✅ TIME thay vì DATETIME
  checkoutTime TIME,        -- ✅ TIME thay vì DATETIME
  PRIMARY KEY(employeeId, processDate),
  FOREIGN KEY (processLogInId) REFERENCES ProcessLog(id) 
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (processLogOutId) REFERENCES ProcessLog(id) 
    ON DELETE CASCADE ON UPDATE CASCADE
);

create table Employee (
	  id int primary key
);

create table Holidays (
  id int auto_increment primary key,
  holidayDate date not null,
  detail varchar(100), 
  type enum('Public', 'Company', 'Others') default 'Public' -- Public: Nghỉ lễ cả nước; Company: do cty quy định; Others: khác
);

create table PayRollClosing (
    id int primary key,       
    periodStart date not null,-- Không cần quan tâm cột này
    periodEnd date not null,				
    created_at timestamp default current_timestamp, -- Thời gian tạo bản ghi
    updated_at timestamp default current_timestamp on update current_timestamp-- Thời gian cập nhật bản ghi
);

CREATE TABLE IF NOT EXISTS DailyWorkTimeAnalysis (
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
    shiftEndTime TIME NOT NULL,
    INDEX idx_employee_date (employeeId, workDate),
    INDEX idx_work_date (workDate)
);


