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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    @Autowired
    private ProcessLogRepository processLogRepository;
    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public void saveRawFromExcel(List<ExcelRowDTO> rawDatas) {
        for(ExcelRowDTO rawData : rawDatas) {
            ProcessLog dbRawLog = new ProcessLog();

            Optional<ProcessLog> row = processLogRepository.findById(dbRawLog.getId());
            if(row.isEmpty()){
                dbRawLog.setId(rawData.getId());
                dbRawLog.setEmpId(rawData.getEmpId());
                dbRawLog.setStartTime(LocalTime.parse(rawData.getStartTime(), timeFmt));
                dbRawLog.setEndTime(LocalTime.parse(rawData.getEndTime(),timeFmt));
                dbRawLog.setDate(LocalDate.parse(rawData.getDate(),dateFmt));
                // checkedTime giờ là String, parse trực tiếp
                dbRawLog.setCheckedTime(LocalTime.parse(rawData.getCheckedTime(),timeFmt));
            }

            processLogRepository.save(dbRawLog);
        }
    }

    public void saveNewFromExcel(List<ExcelExportDTO> newDatas) {
        for(ExcelExportDTO newData : newDatas) {
            AttendanceLog dbNewLog = new AttendanceLog();
            dbNewLog.setEmpId(newData.getEmpId());
            dbNewLog.setProcessDate(LocalDate.parse(newData.getDate(),dateFmt));
            
            // Set checkin ID và time
            if(newData.getCheckinId() > 0) {
                dbNewLog.setProcessLogInId(newData.getCheckinId());
            }
            if(newData.getCheckinTime() != null) {
                dbNewLog.setCheckinTime(LocalTime.parse(newData.getCheckinTime(),timeFmt));
            }
            
            // Set checkout ID và time (có thể null nếu chưa checkout)
            if(newData.getCheckoutId() > 0) {
                dbNewLog.setProcessLogOutId(newData.getCheckoutId());
            }
            if(newData.getCheckoutTime() != null) {
                dbNewLog.setCheckoutTime(LocalTime.parse(newData.getCheckoutTime(),timeFmt));
            }

            attendanceLogRepository.save(dbNewLog);
        }
    }
}
