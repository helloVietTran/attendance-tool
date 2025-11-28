package com.kits.tool.service;

import com.kits.tool.entity.AttendanceLog;
import com.kits.tool.entity.ProcessLog;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.repository.AttendanceLogRepository;
import com.kits.tool.repository.ProcessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    @Autowired
    private ProcessLogRepository processLogRepository;
    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    public void saveRawFromExcel(List<ExcelRowDTO> rawDatas) {
        for(ExcelRowDTO rawData : rawDatas) {
            ProcessLog dbRawLog = new ProcessLog();

            Optional<ProcessLog> row = processLogRepository.findById(rawData.getId());
            if(row.isEmpty()){
                dbRawLog.setId(rawData.getId());
                dbRawLog.setEmpId(rawData.getEmpId());
                dbRawLog.setStartTime(rawData.getStartTime());
                dbRawLog.setEndTime(rawData.getEndTime());
                dbRawLog.setDate(rawData.getDate());
                dbRawLog.setCheckedTime(rawData.getCheckedTime());
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
