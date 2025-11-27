package com.kits.tool.service;

import com.kits.tool.entity.AttendanceLog;
import com.kits.tool.entity.ProcessLog;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.repository.AttendanceLogRepository;
import com.kits.tool.repository.ProcessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    @Autowired
    private ProcessLogRepository processLogRepository;
    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public void saveRawFromExcel(List<ExcelRowDTO> rawDatas) {
        for(ExcelRowDTO rawData : rawDatas) {
            //Tách giờ phút: từ dạng Date gốc về dạng LocalTime
            Date fullTime = rawData.getCheckedTime();
            LocalTime rawTime = fullTime
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();

            //Tách ngày: từ dạng Date gốc về dạng LocalDate
            Date fullDate = rawData.getDate();
            LocalDate rawDate = fullDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            ProcessLog dbRawLog = new ProcessLog();

            Optional<ProcessLog> row = processLogRepository.findById(dbRawLog.getId());
            if(row.isEmpty()){
                dbRawLog.setId(rawData.getId());
                dbRawLog.setEmpId(rawData.getEmpId());
                dbRawLog.setStartTime(rawTime);
                dbRawLog.setEndTime(rawTime);
                dbRawLog.setDate(rawDate);
                dbRawLog.setCheckedTime(LocalTime.parse(sdf.format(rawData.getCheckedTime()),timeFmt));
            }

            processLogRepository.save(dbRawLog);
        }
    }

    public void saveNewFromExcel(List<ExcelExportDTO> newDatas) {
        for(ExcelExportDTO newData : newDatas) {

            AttendanceLog dbNewLog = new AttendanceLog();
            dbNewLog.setEmpId(newData.getEmpId());
            dbNewLog.setProcessLogInId(newData.getCheckinId());
            dbNewLog.setProcessLogOutId(newData.getCheckoutId());
            dbNewLog.setProcessDate(newData.getDate());
            dbNewLog.setCheckinTime(newData.getCheckinTime());
            dbNewLog.setCheckoutTime(newData.getCheckoutTime());

            attendanceLogRepository.save(dbNewLog);
        }
    }
}
